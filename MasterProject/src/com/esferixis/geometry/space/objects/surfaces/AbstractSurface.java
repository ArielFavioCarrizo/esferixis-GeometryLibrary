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
package com.esferixis.geometry.space.objects.surfaces;

import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.space.objects.Line;
import com.esferixis.math.Vector3f;

/**
 * @author ariel
 *
 */
public abstract class AbstractSurface<T extends AbstractSurface<T>> implements Surface<T> {
	/**
	 * @post Crea la superficie
	 */
	public AbstractSurface() {
		
	}
	
	/**
	 * @post Devuelve la intersección de la superficie con la recta
	 * 		 especificada
	 */
	public abstract List<Float> p_intersect(Line line);
	
	/**
	 * @post Devuelve la intersección más pequeña con la superficie
	 * 		 especificada, si no existe devuelve null
	 */
	public Float p_nearestIntersect(Line line) {
		List<Float> intersections = this.p_intersect(line);
		return (intersections != null ? Collections.min(intersections) : null);
	}
	
	/**
	 * @post Devuelve la parametrización de la superficie
	 */
	//public abstract Parametrization parametrization();
	
	/**
	 * @pre La recta con vector dirección igual a la
	 * 		normal que contiene al punto interseca
	 * 		la superficie
	 * @post Devuelve si el punto está del lado
	 * 		 que apunta la normal si no está contenido
	 * 		 en la superficie, caso contrario devolverá true.
	 * 		 Si la precondición no se cumple el resultado
	 * 		 es indeterminado
	 */
	public abstract boolean isNormalSide(Vector3f point);
}
