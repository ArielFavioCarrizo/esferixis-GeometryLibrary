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
package com.esferixis.geometry.space.objects.group;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.space.objects.AffineHolomorphicGeometricObject;
import com.esferixis.geometry.space.objects.ProportionalHolomorphicGeometricObject;
import com.esferixis.math.ProportionalMatrix4f;

/**
 * Objeto geométrico compuesto
 * 
 * @author ariel
 *T>
 */
public class ProportionalHolomorphicGeometricObjectGroup implements ProportionalHolomorphicGeometricObject<ProportionalHolomorphicGeometricObjectGroup> {
	private final List< ProportionalHolomorphicGeometricObject<?> > elements;
	
	/**
	 * @pre Los elementos no pueden ser nulos y tienen que ser de la clase
	 * 		de elemento especificada
	 * @post Crea el grupo con los objetos especificados
	 */
	public ProportionalHolomorphicGeometricObjectGroup(ProportionalHolomorphicGeometricObject<?>... elements) {
		if ( elements != null ) {
			elements = elements.clone();
			
			for ( ProportionalHolomorphicGeometricObject<?> eachElement : elements ) {
				if ( eachElement == null ) {
					throw new NullPointerException();
				}
			}
			
			this.elements = Collections.unmodifiableList( Arrays.asList(elements) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve los elementos (Sólo lectura)
	 */
	public List< ProportionalHolomorphicGeometricObject<?> > getElementransformMatrixts() {
		return this.elements;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public ProportionalHolomorphicGeometricObjectGroup transform(
			ProportionalMatrix4f transformMatrix) {
		if ( transformMatrix != null ) {
			int i=0;
			ProportionalHolomorphicGeometricObject<?>[] elements = new ProportionalHolomorphicGeometricObject[this.elements.size()];
			for ( ProportionalHolomorphicGeometricObject<?> eachElement : elements ) {
				elements[i++] = eachElement.transform(transformMatrix);
			}
			
			return new ProportionalHolomorphicGeometricObjectGroup(elements);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject#accept(com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.geometry.space.objects.ProportionalHolomorphicGeometricObject.Visitor<V> visitor) {
		return visitor.visit(this);
	}
}
