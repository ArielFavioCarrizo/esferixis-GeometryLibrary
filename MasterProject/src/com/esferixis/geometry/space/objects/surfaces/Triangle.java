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

import com.esferixis.geometry.space.VerticesList;
import com.esferixis.geometry.space.objects.AffineHolomorphicGeometricObject;
import com.esferixis.geometry.space.objects.Line;
import com.esferixis.geometry.space.objects.group.AffineHolomorphicGeometricObjectGroup;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.collection.set.ArraySet;

public final class Triangle extends AbstractSurface<Triangle> implements AffineHolomorphicGeometricObject<Triangle> {
	private final VerticesList vertices;
	private Plane containingPlane;
	
	/**
	 * @post Crea un triángulo con los 3 vértices especificados
	 */
	public Triangle(Vector3f v1, Vector3f v2, Vector3f v3) {
		this.vertices = new VerticesList(v1, v2, v3);
		this.containingPlane = null;
	}
	
	/**
	 * @post Devuelve los vértices
	 */
	public VerticesList getVertices() {
		return this.vertices;
	}
	
	/**
	 * @post Devuelve el plano contenedor
	 */
	public final Plane getContainingPlane() {
		// Calcula el plano por única vez
		if ( this.containingPlane == null ) {
			// Calcula la normal: normal = (v2-v1)^(v3-v1)
			this.containingPlane = new Plane(this.vertices.get(0), this.vertices.get(1).sub(this.vertices.get(0)).cross( this.vertices.get(2).sub(this.vertices.get(0))).normalise() );
		}
		return this.containingPlane;
	}

	/* (non-Javadoc)
	 * @see se3d.geometry.objects.SpaceSet#transform(math.Matrix4f)
	 */
	@Override
	public Triangle transform(Matrix4f transformMatrix) {
		return new Triangle( transformMatrix.transformPoint( this.getVertices().get(0) ), transformMatrix.transformPoint( this.getVertices().get(1) ), transformMatrix.transformPoint( this.getVertices().get(2) ) );
	}
	
	/**
	 * @post Devuelve un triángulo con el sentido invertido
	 */
	public Triangle oppositeNormal() {
		Triangle oppositeTriangle = new Triangle(this.vertices.get(2), this.vertices.get(1), this.vertices.get(0));
		if ( this.containingPlane != null ) {
			oppositeTriangle.containingPlane = this.containingPlane.oppositePlane();
		}
		return oppositeTriangle;
	}
	
	/**
	 * @post Crea un cuadrilátero compuesto por dos triángulos
	 */
	public static AffineHolomorphicGeometricObject<? extends AffineHolomorphicGeometricObject<?>> createQuadrilateral(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
		return new AffineHolomorphicGeometricObjectGroup(new Triangle(a, b, c), new Triangle(a, c, d));
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.vertices.hashCode() + this.getContainingPlane().getNormal().hashCode();
	}
	
	/**
	 * @post Devuelve si es igual a otro objeto
	 */
	@Override
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Triangle ) {
				Triangle otherTriangle = (Triangle) other;
				return (new ArraySet<Vector3f>(this.vertices)).equals(new ArraySet<Vector3f>(otherTriangle.vertices)) && this.containingPlane.getNormal().equals(otherTriangle.getContainingPlane().getNormal());
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see se3d.geometry.objects.surfaces.Surface#isNormalSide(math.Vector3f)
	 */
	@Override
	public boolean isNormalSide(Vector3f point) {
		return this.getContainingPlane().isNormalSide(point);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.Surface#getIntersection(com.esferixis.geometry.objects.Line)
	 */
	@Override
	public List<Float> p_intersect(Line line) {
		List<Float> resultPlane = this.getContainingPlane().p_intersect(line);
		if ( !resultPlane.isEmpty() ) {
			final Vector3f resultPoint = line.getReferencePoint().add(line.getDirection().scale(resultPlane.get(0))).sub(this.getVertices().get(0));
			Vector3f v_01 = this.getVertices().get(1).sub(this.getVertices().get(0));
			Vector3f v_02 = this.getVertices().get(2).sub(this.getVertices().get(0));
			float s_01 = v_01.scalarProjection(resultPoint);
			float s_02 = v_01.scalarProjection(resultPoint);
			
			if ( ExtraMath.containedByInterval(s_01 * s_01, 0.0f, v_01.lengthSquared()) && ExtraMath.containedByInterval(s_02 * s_02, 0.0f, v_02.lengthSquared()) ) {
				return resultPlane;
			}
			else {
				return Collections.emptyList();
			}
		}
		else {
			return Collections.emptyList();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.SimpleSpaceSet#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public Triangle transform(ProportionalMatrix4f transformMatrix) {
		return this.transform((Matrix4f) transformMatrix);
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
