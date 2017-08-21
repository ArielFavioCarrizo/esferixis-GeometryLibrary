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

import com.esferixis.math.Matrix4f;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.Vector3f;

public final class Point implements AffineHolomorphicGeometricObject<Point> {
	private final Vector3f vertex; // Vértice
	
	/**
	 * @post Crea un punto con el vértice especificado
	 */
	public Point(Vector3f vertex) {
		this.vertex = vertex;
	}
	
	/**
	 * @post Devuelve el vértice
	 */
	public Vector3f getVertex() {
		return this.vertex;
	}
	
	/* (non-Javadoc)
	 * @see se3d.geometry.objects.SpaceSet#transform(math.Matrix4f)
	 */
	@Override
	public Point transform(Matrix4f transformMatrix) {
		return new Point( transformMatrix.transformPoint(this.vertex) );
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public Point transform(ProportionalMatrix4f transformMatrix) {
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
