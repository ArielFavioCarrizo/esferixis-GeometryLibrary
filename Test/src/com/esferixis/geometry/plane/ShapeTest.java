/**
 * Copyright (c) 2017 Ariel Favio Carrizo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'esferixis' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.esferixis.geometry.plane;
import java.util.Scanner;

import com.esferixis.geometry.plane.Shape;
import com.esferixis.geometry.plane.exception.ProportionalHolomorphicShapeParseException;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * @author ariel
 *
 */
public final class ShapeTest {
	private ShapeTest() {}
	
	public static <S extends Shape<?>> S enterShape(Scanner scanner, String name, Class<S> shapeClass) {
		boolean enterAffine = shapeClass.equals(FiniteAffineHolomorphicShape.class);
		
		if ( ( scanner != null ) && ( name != null ) && ( shapeClass != null )) {
			Shape<?> shape = null;
			
			boolean parseError;
			
			do {
				System.out.println("Enter '" + name + "': ");
				
				try {
					final String shapeString = scanner.nextLine();
					
					parseError = false;
					
					if ( !shapeString.isEmpty() ) {
						shape = Shape.parse(shapeString, Shape.class);
						
						if ( enterAffine ) {
							if ( ( shape instanceof FiniteProportionalHolomorphicShape ) && FiniteAffineHolomorphicShape.isCasteable( (FiniteProportionalHolomorphicShape<?>) shape ) ) {
								shape = FiniteAffineHolomorphicShape.cast( (FiniteProportionalHolomorphicShape<?>) shape);
							}
							else {
								System.out.println("Expected affine shape");
								parseError = true;
							}
						}
						else {
							if ( !shapeClass.isInstance(shape) ) {
								System.out.println("Expected " + shapeClass);
								parseError = true;
							}
						}
					}
					else {
						shape = null;
					}
				} catch ( ProportionalHolomorphicShapeParseException | NumberFormatException e ) {
					System.out.println("Parse error: " + e.getMessage());
					parseError = true;
				}
			} while ( parseError );
			
			return (S) shape;
		}
		else {
			throw new NullPointerException();
		}
	}
}
