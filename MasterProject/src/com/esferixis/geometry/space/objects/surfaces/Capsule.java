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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.space.objects.Edge;
import com.esferixis.geometry.space.objects.Line;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.QuadraticEquation;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.collection.list.BinaryList;

/**
 * Cápsula
 * 
 * @author ariel
 *
 */
public final class Capsule extends AbstractSurface<Capsule> {
	private final Edge axis;
	private float radius;
	
	/**
	 * @pre El eje no puede ser nulo
	 * @post Crea una cápsula con el eje y el radio especificados
	 */
	public Capsule(Edge axis, float radius) {
		if ( axis != null ) {
			this.axis = axis;
			this.radius = radius;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el eje
	 */
	public Edge getAxis() {
		return this.axis;
	}
	
	/**
	 * @post Devuelve el radio
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.Surface#p_intersect(com.esferixis.geometry.objects.Line)
	 */
	@Override
	public List<Float> p_intersect(Line line) {
		if ( line != null ) {
			// Comprueba la colisión con el cilindro infinito
			final Vector3f A = this.axis.getVertices().get(0);
			final Vector3f AB = this.axis.getVertices().get(1).sub(this.axis.getVertices().get(0));
			final Vector3f AO = this.axis.getVertices().get(0).sub(line.getReferencePoint());
			final Vector3f d = line.getDirection();
			
			final float m = AB.dot(d) / AB.lengthSquared();
			final float n = AB.dot(AO) / AB.lengthSquared();
			
			final Vector3f Q = d.sub(AB.scale(m));
			final Vector3f R = AO.sub(AB.scale(n));
			
			final Sphere sphere1 = new Sphere(this.axis.getVertices().get(0), this.radius);
			final Sphere sphere2 = new Sphere(this.axis.getVertices().get(1), this.radius);
			
			List<Float> candidateIntersections = new ArrayList<Float>(4);
			
			if ( Q.lengthSquared() != 0.0f ) { // Si d y AB no son paralelos
				/**
				 * Aquellas intersecciones que estén entre las dos esferas se
				 * agregarán como intersecciones candidatas, y aquellas
				 * que no pertenezcan se tomarán como referencia para
				 * hallar intersecciones con la esfera asociada correspondiente
				 */
				final List<Float> infiniteCylinderIntersections = QuadraticEquation.resolve(Q.lengthSquared(), 2.0f * Q.dot(R), R.lengthSquared() - this.radius * this.radius);
				for ( Float eachIntersection : infiniteCylinderIntersections ) {
					final Vector3f eachIntersectionPoint = line.getPoint(eachIntersection);
					
					float projection = AB.scalarProjection(eachIntersectionPoint.sub(A));
					if ( projection < 0 ) {
						candidateIntersections.addAll(sphere1.p_intersect(line));
					}
					else if ( projection <= 1 ) {
						candidateIntersections.add(eachIntersection);
					}
					else {
						candidateIntersections.addAll(sphere2.p_intersect(line));
					}
				}
			}
			else { // Si son paralelos
				// Solamente pueden intersecar con las esferas
				candidateIntersections.addAll(sphere1.p_intersect(line));
				candidateIntersections.addAll(sphere2.p_intersect(line));
			}
			
			return new BinaryList<Float>(Collections.min(candidateIntersections), Collections.max(candidateIntersections));
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.Surface#isNormalSide(com.esferixis.math.Vector3f)
	 */
	@Override
	public boolean isNormalSide(Vector3f point) {
		if ( point != null ) {
			return ( this.axis.distanceToPointSquared(point) > this.radius * this.radius );
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalAffineTransformableSpaceSet#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public Capsule transform(ProportionalMatrix4f transformMatrix) {
		return new Capsule(this.axis.transform(transformMatrix), transformMatrix.transformScalar(this.radius));
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
