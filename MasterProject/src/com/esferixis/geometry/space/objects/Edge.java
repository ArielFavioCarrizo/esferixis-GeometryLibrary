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

package com.esferixis.geometry.space.objects;

import com.esferixis.geometry.space.VerticesList;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.Vector3f;

/**
 * Segmento en el juego de Super Esferix 3D
 *  
 * @author ariel
 *
 */
public final class Edge implements AffineHolomorphicGeometricObject<Edge> {
	
	private final VerticesList vertices;

	public Edge(Vector3f v1, Vector3f v2) {
		this.vertices = new VerticesList(v1, v2);
	}
	
	/**
	 * @post Devuelve los vértices
	 */
	public VerticesList getVertices() {
		return this.vertices;
	}
	
	/* (non-Javadoc)
	 * @see se3d.geometry.objects.SpaceSet#transform(math.Matrix4f)
	 */
	@Override
	public Edge transform(Matrix4f transformMatrix) {
		return new Edge( transformMatrix.transformPoint(this.vertices.get(0)), transformMatrix.transformPoint(this.vertices.get(1)));
	}
	
	/**
	 * @post Devuelve el cuadrado de su longitud
	 */
	public float getSquaredLength() {
		return this.vertices.get(1).sub(this.vertices.get(0)).lengthSquared();
	}
	
	/**
	 * @post Devuelve el cuadrado de la distancia a un punto
	 */
	public float distanceToPointSquared(Vector3f point) {
		Vector3f delta_10 = this.vertices.get(1).sub(this.vertices.get(0));
		Vector3f delta_p0 = point.sub(this.vertices.get(0));
		float t = delta_10.scalarProjection(delta_p0);
		
		if ( t < 0.0f ) {
			t = 0.0f;
		} else if ( t > 1.0f ) {
			t = 1.0f;
		}
		
		return delta_10.scale(t).sub(delta_p0).lengthSquared();
	}
	
	/**
	 * @post Devuelve la recta contenedora
	 */
	public Line getContainingLine() {
		return new Line(this.vertices.get(0), this.vertices.get(1).sub(this.vertices.get(0)));
	}
	
	/**
	 * @post Devuelve el hash
	 */
	public int hashCode() {
		return this.vertices.hashCode();
	}
	
	/**
	 * @post Devuelve si es igual a otro objeto
	 */
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Edge ) {
				return this.vertices.equals( ( (Edge) other ).vertices );
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * @pre La matriz no puede ser nula
	 * @post Devuelve una transfomación del conjunto de puntos con la matriz de
	 * 		 transformación especificada
	 */
	@Override
	public Edge transform(ProportionalMatrix4f transformMatrix) {
		return this.transform( (Matrix4f) transformMatrix);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject#accept(com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.geometry.space.objects.ProportionalHolomorphicGeometricObject.Visitor<V> visitor) {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.AffineHolomorphicGeometricObject#accept(com.esferixis.geometry.objects.AffineHolomorphicGeometricObject.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.geometry.space.objects.AffineHolomorphicGeometricObject.Visitor<V> visitor) {
		return visitor.visit(this);
	}
}
