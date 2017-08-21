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
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class LineSegment extends Curve<LineSegment> implements FiniteAffineHolomorphicShape.Casteable<LineSegment> {
	private static final long serialVersionUID = 4224162922085459177L;
	
	private final Vector2f point1, point2;
	
	/**
	 * @pre Los puntos no pueden ser nulos
	 * @post Crea la línea con los puntos especificados
	 */
	public LineSegment(Vector2f point1, Vector2f point2) {
		if ( ( point1 != null ) && ( point2 != null ) ) {
			this.point1 = point1;
			this.point2 = point2;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el punto 1
	 */
	public Vector2f getPoint1() {
		return this.point1;
	}
	
	/**
	 * @post Devuelve el punto 2
	 */
	public Vector2f getPoint2() {
		return this.point2;
	}
	
	/**
	 * @post Devuelve el centor
	 */
	public Vector2f getCenter() {
		return this.point1.add(this.point2).scale(0.5f);
	}
	
	/**
	 * @post Devuelve el segmento con los vértices en el orden inverso
	 */
	public LineSegment oppositeOrder() {
		return new LineSegment(this.getPoint2(), this.getPoint1());
	}
	
	/**
	 * @post Devuelve la recta que la contiene,
	 * 		 la normal es el vector AB rotado 90°
	 */
	public Line getRect() {
		return new Line(this.point1, this.point2.sub(this.point1));
	}
	
	/**
	 * @post Devuelve el vector
	 */
	public Vector2f getVector12() {
		return this.point2.sub(this.point1);
	}
	
	/**
	 * @post Devuelve si el punto especificado sobre la recta de la línea, está contenido
	 */
	public boolean contains(float parameter) {
		return new FloatClosedInterval(0.0f, 1.0f).contains(parameter);
	}
	
	/**
	 * @post Devuelve si el punto especificado sobre la recta de la línea, está contenido.
	 * 		 Exceptuando los extremos
	 */
	public boolean containsExcludingExtremes(float parameter) {
		return new FloatClosedInterval(0.0f, 1.0f).containsExcludingExtremes(parameter);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public LineSegment transform(ProportionalMatrix3f matrix) {
		return new LineSegment(matrix.transformPoint(this.point1), matrix.transformPoint(this.point2));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public LineSegment translate(Vector2f displacement) {
		return new LineSegment(this.point1.add(displacement), this.point2.add(displacement));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(Line line) {
		final Float intersectionWithSpecifiedLine = this.getRectIntersectionPoint(line);
		
		final List<Float> result = new ArrayList<Float>(1);
		
		if ( intersectionWithSpecifiedLine != null ) {
			result.add(intersectionWithSpecifiedLine);
		}
		
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * @post Devuelve la intersección con la recta especificada
	 */
	public Float getRectIntersectionPoint(Line line) {
		Float intersectionWithSpecifiedRect = null;
		
		final Float intersectionWithThisLine = line.getRectIntersectionPoint(this.getRect());
		
		if ( ( intersectionWithThisLine != null ) && ( new FloatClosedInterval(0.0f, 1.0f).contains(intersectionWithThisLine) ) ) {
			intersectionWithSpecifiedRect = this.getRect().getRectIntersectionPoint(line);
		}
		
		return intersectionWithSpecifiedRect;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getPoint1().hashCode() + 31 * this.getPoint2().hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof LineSegment ) ) {
			final LineSegment otherLine = (LineSegment) other;
			
			return otherLine.getPoint1().equals(this.getPoint1()) && otherLine.getPoint2().equals(this.getPoint2());
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "LineSegment( " + this.getPoint1() + ", " + this.getPoint2() + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getPoint2().sub(this.getPoint1()).scale(0.5f).add(this.getPoint1());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return this.getRect().contains(point) && ( new FloatClosedInterval(0.0f, 1.0f).contains(this.getVector12().scalarProjection(point.sub(this.getPoint1()))) );
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Curve#getParametrization()
	 */
	@Override
	public com.esferixis.geometry.plane.finite.Curve.Parametrization getParametrization() {
		return new Parametrization(new FloatClosedInterval(0.0f, 1.0f)) {

			@Override
			public Vector2f getPoint(float parameter) {
				return LineSegment.this.getRect().getPointByProportionalScalar(parameter);
			}
			
		};
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
	public FiniteAffineHolomorphicShape<LineSegment> castToAffine() {
		return new FiniteAffineHolomorphicShape<LineSegment>(this) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2734976026815994252L;

			@Override
			protected FiniteAffineHolomorphicShape<LineSegment> castToAffine(LineSegment shape) {
				return shape.castToAffine();
			}

			@Override
			protected LineSegment transform_backingShape(Matrix3f matrix) {
				return new LineSegment(matrix.transformPoint(LineSegment.this.getPoint1()), matrix.transformPoint(LineSegment.this.getPoint2()));
			}

			@Override
			public <V, T extends Throwable> V accept(
					com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape.Visitor<V, T> visitor)
					throws T {
				return visitor.visitLineSegment(this);
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
	 * @see com.arielcarrizo.geometry.plane.finite.Curve#accept(com.arielcarrizo.geometry.plane.finite.Curve.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(com.esferixis.geometry.plane.finite.Curve.Visitor<V, T> visitor)
			throws T {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getBoundingBox()
	 */
	@Override
	public BoundingBox boundingBox() {
		return new BoundingBox(
			new Vector2f(Math.min(this.point1.getX(), this.point2.getX()), Math.min(this.point1.getY(), this.point2.getY())),
			new Vector2f(Math.max(this.point1.getX(), this.point2.getX()), Math.max(this.point1.getY(), this.point2.getY()))
		);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		return Math.max(this.getPoint1().length(), this.getPoint2().length());
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public float minDistanceToOrigin() {
		final Line perpendicularLine = new Line(Vector2f.ZERO, this.getVector12().rotate90AnticlockWise());
		final float minDistance;
		
		final Float intersectionScalar = this.getRect().getRectIntersectionPoint(perpendicularLine);
		
		if ( this.getRectIntersectionPoint(perpendicularLine) != null ) {
			minDistance = Math.abs( intersectionScalar * perpendicularLine.getDirection().length() );
		}
		else {
			minDistance = Math.min(this.point1.length(), this.point2.length());
		}
		
		return minDistance;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public LineSegment opposite() {
		return new LineSegment(this.point1.opposite(), this.point2.opposite());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		final Line perpendicularLine = new Line(Vector2f.ZERO, this.getVector12().rotate90AnticlockWise());
		final float minDistance;
		
		final Float intersectionScalar = this.getRect().getRectIntersectionPoint(perpendicularLine);
		final Vector2f normal;
		
		if ( this.getRectIntersectionPoint(perpendicularLine) != null ) {
			minDistance = Math.abs( intersectionScalar * perpendicularLine.getDirection().length() );
		}
		else {
			minDistance = Math.min(this.point1.length(), this.point2.length());
		}
			
		if ( intersectionScalar > 0.0f ) {
			normal = this.getVector12().rotate90ClockWise();
		}
		else {
			normal = this.getVector12().rotate90AnticlockWise();
		}
		
		return new NearestNormal(normal, minDistance);
	}
}
