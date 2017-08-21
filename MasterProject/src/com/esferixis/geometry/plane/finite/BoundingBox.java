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
package com.esferixis.geometry.plane.finite;

import java.io.Serializable;

import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
public final class BoundingBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3766944783438322421L;
	
	private final Vector2f vertex11, vertex22;
	
	/**
	 * @pre Ninguno de los dos vértices pueden ser nulos
	 * @post Crea el bounding box con los vértices especificados
	 */
	BoundingBox(Vector2f p11, Vector2f p22) {
		if ( ( p11 != null ) && ( p22 != null ) ) {
			this.vertex11 = p11;
			this.vertex22 = p22;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el vértice 11
	 */
	public Vector2f getVertex11() {
		return this.vertex11;
	}
	
	/**
	 * @post Devuelve el vértice 21
	 */
	public Vector2f getVertex21() {
		return new Vector2f(this.vertex22.getX(), this.vertex11.getY());
	}
	
	/**
	 * @post Devuelve el vértice 22
	 */
	public Vector2f getVertex22() {
		return this.vertex22;
	}
	
	/**
	 * @post Devuelve el vértice 12
	 */
	public Vector2f getVertex12() {
		return new Vector2f(this.vertex11.getX(), this.vertex22.getY());
	}
	
	/**
	 * @pre El bounding box especificado no puede ser nulo
	 * @post Calcula el bounding box con el bounding box especificado
	 */
	public BoundingBox unionBoundingBox(BoundingBox other) {
		if ( other != null ) {
			return new BoundingBox(
					new Vector2f(this.vertex11.getX() + other.vertex11.getX(), this.vertex11.getY() + other.vertex11.getY()),
					new Vector2f(this.vertex22.getX() + other.vertex22.getX(), this.vertex22.getY() + other.vertex22.getY())
			);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el polígono convexo asociado
	 */
	public ConvexPolygon getConvexPolygon() {
		return new ConvexPolygon(this.getVertex11(), this.getVertex21(), this.getVertex22(), this.getVertex12());
	}
}
