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

import java.util.List;

import com.esferixis.geometry.space.objects.Line;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.QuadraticEquation;
import com.esferixis.math.Vector3f;

/**
 * Esfera
 * 
 * @author ariel
 *
 */
public final class Sphere extends AbstractSurface<Sphere> {
	private final Vector3f center;
	private final float radius;
	
	/**
	 * @post Crea la esfera con la posición y el radio especificados
	 */
	public Sphere(Vector3f center, float radius) {
		this.center = center;
		this.radius = radius;
	}
	
	/**
	 * @post Devuelve el centro
	 */
	public Vector3f getCenter() {
		return this.center;
	}
	
	/**
	 * @post Devuelve el radio
	 */
	public float getRadius() {
		return this.radius;
	}
	
	@Override
	public boolean isNormalSide(Vector3f point) {
		return point.sub(this.center).lengthSquared() > this.radius * this.radius;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.Surface#p_intersect(com.esferixis.geometry.objects.Line)
	 */
	@Override
	public List<Float> p_intersect(Line line) {
		Vector3f linesphere_origin_delta = line.getReferencePoint().sub(this.center);
		return QuadraticEquation.resolve(line.getDirection().lengthSquared(), 2.0f * (line.getDirection().dot(linesphere_origin_delta)), linesphere_origin_delta.lengthSquared() - this.radius * this.radius);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.SimpleSpaceSet#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public Sphere transform(ProportionalMatrix4f transformMatrix) {
		return new Sphere(transformMatrix.transformPoint(this.center), transformMatrix.transformScalar(this.radius));
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
