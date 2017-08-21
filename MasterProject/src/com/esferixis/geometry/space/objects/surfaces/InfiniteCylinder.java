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
 * @author ariel
 *
 */
public final class InfiniteCylinder extends AbstractSurface<InfiniteCylinder> {
	private Line axis;
	private float radius;
	
	/**
	 * @pre La recta no puede ser nula
	 * @post Crea un cilindro infinito con la recta y el radio especificados
	 * @throws NullPointerException
	 */
	public InfiniteCylinder(Line axis, float radius) {
		if ( axis != null ) {
			this.axis = axis;
			this.radius = radius;
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalAffineTransformableSpaceSet#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public InfiniteCylinder transform(ProportionalMatrix4f transformMatrix) {
		if ( transformMatrix != null ) {
			return new InfiniteCylinder(axis.transform(transformMatrix), transformMatrix.transformScalar(this.radius));
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.AbstractSurface#p_intersect(com.esferixis.geometry.objects.Line)
	 */
	@Override
	public List<Float> p_intersect(Line line) {
		Vector3f dist_p0_axis_line = this.axis.getReferencePoint().sub(line.getReferencePoint());
		final float m = this.axis.getDirection().dot(line.getDirection()) / this.axis.getDirection().lengthSquared();
		final float n = this.axis.getDirection().dot(dist_p0_axis_line) / this.axis.getDirection().lengthSquared();
		Vector3f Q = line.getDirection().sub(this.axis.getDirection().scale(m));
		Vector3f R = dist_p0_axis_line.sub(this.axis.getDirection().scale(n));
		return QuadraticEquation.resolve(Q.lengthSquared(), 2.0f * Q.dot(R), R.lengthSquared() - this.radius * this.radius);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.AbstractSurface#isNormalSide(com.esferixis.math.Vector3f)
	 */
	@Override
	public boolean isNormalSide(Vector3f point) {
		return this.axis.distanceToPointSquared(point) > this.radius * this.radius;
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
