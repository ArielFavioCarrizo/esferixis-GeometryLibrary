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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.plane.Line;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
public final class Point extends FiniteProportionalHolomorphicShape<Point> implements FiniteAffineHolomorphicShape.Casteable<Point> {
	private static final long serialVersionUID = -3191471110764193187L;
	
	private final Vector2f position;
	
	/**
	 * @pre La posición no puede ser nula
	 * @post Crea el punto con la posición especificada
	 */
	public Point(Vector2f position) {
		if ( position != null ) {
			this.position = position;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la posición
	 */
	public Vector2f getPosition() {
		return this.position;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public Point transform(ProportionalMatrix3f matrix) {
		return new Point(matrix.transformPoint(this.position));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public Point translate(Vector2f displacement) {
		return new Point(this.position.add(displacement));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRayIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public Float getRayIntersection(Line rect) {
		if ( rect != null ) {
			final Vector2f referenceToPoint = this.getPosition().sub( rect.getReferencePoint() );
			Float t = null;
			
			if ( referenceToPoint.isParallel(rect.getDirection()) ) {
				float scalarProjection = rect.getDirection().normalise().scalarProjection(referenceToPoint);
				
				if ( scalarProjection >= 0.0f ) {
					t = scalarProjection;
				}
			}
			
			return t;
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(Line rect) {
		if ( rect != null ) {
			final List<Float> result = new ArrayList<Float>(1);
			
			final Vector2f referenceToPoint = this.getPosition().sub( rect.getReferencePoint() );
			
			if ( referenceToPoint.isParallel(rect.getDirection()) ) {
				result.add( rect.getDirection().normalise().scalarProjection(referenceToPoint) );
			}
			
			return Collections.unmodifiableList(result);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getPosition().hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof Point ) ) {
			Point otherPoint = (Point) other;
			
			return otherPoint.getPosition().equals(this.getPosition());
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Devuelve la representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "Point( " + this.getPosition() + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getPosition();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return this.getPosition().equals(point);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#accept(com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(
			com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape.Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.AffineHolomorphicShape.Casteable#castToAffine()
	 */
	@Override
	public FiniteAffineHolomorphicShape<Point> castToAffine() {
		return new FiniteAffineHolomorphicShape<Point>(this) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -708686220084542492L;

			@Override
			protected FiniteAffineHolomorphicShape<Point> castToAffine(Point shape) {
				return shape.castToAffine();
			}

			@Override
			protected Point transform_backingShape(Matrix3f matrix) {
				return new Point(matrix.transformPoint(Point.this.getPosition()));
			}

			@Override
			public <V, T extends Throwable> V accept(
					com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape.Visitor<V, T> visitor)
					throws T {
				return visitor.visitPoint(this);
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getBoundingAffineHolomorphicShape()
	 */
	@Override
	public FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> getBoundingAffineHolomorphicShape() {
		return this.castToAffine();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#accept(com.arielcarrizo.geometry.plane.Shape.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(com.esferixis.geometry.plane.Shape.Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getBoundingBox()
	 */
	@Override
	public BoundingBox boundingBox() {
		return new BoundingBox(this.position, this.position);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		return this.position.length();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public Point opposite() {
		return new Point(this.position.opposite());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public float minDistanceToOrigin() {
		return this.position.length();
	}
}
