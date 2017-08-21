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

import com.esferixis.geometry.Geometry;
import com.esferixis.geometry.plane.Line;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class CircumferenceSegment extends Curve<CircumferenceSegment> {
	private static final long serialVersionUID = -4357380142911161394L;
	
	private Circumference circumference;
	private final FloatClosedInterval angleInterval;
	
	/**
	 * @post Crea el segmento de circunferencia con la circunferencia y el intervalo de ángulos
	 * 		 especificado
	 */
	public CircumferenceSegment(Circumference circumference, FloatClosedInterval angleInterval) {
		if ( ( circumference != null ) && ( angleInterval != null ) ) {
			if ( angleInterval.length() > ExtraMath.doublePI) {
				angleInterval = new FloatClosedInterval(angleInterval.getMin(), angleInterval.getMax() + ExtraMath.doublePI);
			}
			
			this.circumference = circumference;
			this.angleInterval = angleInterval;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el punto límite con el ángulo más pequeño
	 */
	public Vector2f getLimitPoint1() {
		return this.circumference.getPointWithAngle(this.angleInterval.getMin());
	}
	
	/**
	 * @post Devuelve el punto límite con el ángulo más grande
	 */
	public Vector2f getLimitPoint2() {
		return this.circumference.getPointWithAngle(this.angleInterval.getMax());
	}
	
	/**
	 * @post Devuelve el intervalo de ángulos
	 */
	public FloatClosedInterval getAngleInterval() {
		return this.angleInterval;
	}
	
	/**
	 * @post Devuelve la circunferencia
	 */
	public Circumference getCircumference() {
		return this.circumference;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public CircumferenceSegment transform(ProportionalMatrix3f matrix) {
		final float angleDisplacement = matrix.getAngleDisplacement();
		
		return new CircumferenceSegment(circumference.transform(matrix), new FloatClosedInterval(angleInterval.getMin() + angleDisplacement, angleInterval.getMax() + angleDisplacement) );
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public CircumferenceSegment translate(Vector2f displacement) {
		return new CircumferenceSegment(this.circumference.translate(displacement), this.angleInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(final Line rect) {
		if ( rect != null ) {
			final List<Float> intersections = this.getCircumference().getRectIntersection(rect);
			final List<Float> resultIntersections = new ArrayList<Float>(intersections.size());
			
			for ( Float eachIntersection : intersections ) {
				final Vector2f eachPoint = rect.getPointByProportionalScalar(eachIntersection);
				
				if ( Geometry.containsAngle(this.getAngleInterval(), eachPoint.sub(this.getCircumference().getCenter()).getAngle()) ) {
					resultIntersections.add(eachIntersection);
				}
			}
			
			return Collections.unmodifiableList(resultIntersections);
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
		return this.getCircumference().hashCode() * 31 + this.angleInterval.hashCode();
	}
	
	/**
	 * @postr Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof CircumferenceSegment ) ) {
			final CircumferenceSegment otherCircumferenceSegment = (CircumferenceSegment) other;
			
			return otherCircumferenceSegment.getCircumference().equals(this.getCircumference()) && otherCircumferenceSegment.equals(this.angleInterval);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Devuelve la conversión a cadena de carácteres
	 */
	@Override
	public String toString() {
		return "CircumferenceSegment( " + this.getCircumference() + ", " + this.getAngleInterval() + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.circumference.getPointWithAngle(this.angleInterval.midPoint());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return this.getCircumference().contains(point) && Geometry.containsAngle(this.getAngleInterval(), point.sub(this.getCircumference().getCenter()).getAngle());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Curve#getParametrization()
	 */
	@Override
	public com.esferixis.geometry.plane.finite.Curve.Parametrization getParametrization() {
		return new Parametrization(this.getAngleInterval()) {

			@Override
			public Vector2f getPoint(float parameter) {
				return CircumferenceSegment.this.getCircumference().getPointWithAngle(parameter);
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
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getBoundingAffineHolomorphicShape()
	 */
	@Override
	public FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> getBoundingAffineHolomorphicShape() {
		final float radius = this.getCircumference().getRadius();
		
		float[] tangentVertexAngles = Geometry.partialAngleIntervalSampling(this.getAngleInterval());
		
		final FiniteAffineHolomorphicShape<ConvexPolygon>[] boundingShapes = new FiniteAffineHolomorphicShape[tangentVertexAngles.length-1];
		
		Vector2f unitVertex1 = null;
		Vector2f unitVertex2 = null;
		Vector2f vertex1 = null;
		Vector2f vertex2 = null;
		
		for ( int i = 0 ; i<tangentVertexAngles.length ; i++ ) {
			unitVertex2 = Vector2f.getUnitVectorWithAngle(tangentVertexAngles[i]);
			vertex2 = this.getCircumference().getCenter().add(unitVertex2.scale(radius));
			
			if ( i != 0 ) {
				final Line line1 = new Line(vertex1, unitVertex1.rotate90AnticlockWise());
				final Line line2 = new Line(vertex2, unitVertex2.rotate90ClockWise());
				
				final Vector2f intermediateVertex = line1.getReferencePoint().add(line1.getDirection().scale(line1.getRectIntersectionPoint(line2)));
				
				boundingShapes[i-1] = new ConvexPolygon(vertex1, intermediateVertex, vertex2).castToAffine();
			}
			
			unitVertex1 = unitVertex2;
			vertex1 = vertex2;
		}
		
		return FiniteProportionalHolomorphicShapeGroup.castToAffine( new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends ConvexPolygon>>(boundingShapes) );
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
		final float minX, maxX;
		final float minY, maxY;
		
		final Vector2f limitPoint1 = this.getLimitPoint1();
		final Vector2f limitPoint2 = this.getLimitPoint2();
		
		if ( Geometry.containsAngle(angleInterval, (float) Math.PI) ) {
			minX = this.circumference.getCenter().getX() - this.circumference.getRadius();
		}
		else {
			minX = Math.min(limitPoint1.getX(), limitPoint2.getX());
		}
		
		if ( Geometry.containsAngle(angleInterval, 0.0f) ) {
			maxX = this.circumference.getCenter().getX() + this.circumference.getRadius();
		}
		else {
			maxX = Math.min(limitPoint1.getX(), limitPoint2.getX());
		}
		
		if ( Geometry.containsAngle(angleInterval, -(float) Math.PI / 2.0f) ) {
			minY = this.circumference.getCenter().getY() - this.circumference.getRadius();
		}
		else {
			minY = Math.min(limitPoint1.getY(), limitPoint2.getY());
		}
		
		if ( Geometry.containsAngle(angleInterval, (float) Math.PI / 2.0f) ) {
			maxY = this.circumference.getCenter().getY() + this.circumference.getRadius();
		}
		else {
			maxY = Math.min(limitPoint1.getY(), limitPoint2.getY());
		}
		
		return new BoundingBox(new Vector2f(minX, minY), new Vector2f(maxX, maxY));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		final float centerAngle = this.circumference.getCenter().getAngle();
		final float result;
		
		if ( Geometry.containsAngle(this.angleInterval, centerAngle) ) {
			result = this.circumference.getCenter().length() + this.circumference.getRadius();
		}
		else {
			result = Math.max(this.getLimitPoint1().length(), this.getLimitPoint2().length());
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		final Vector2f originRelativePosition = this.circumference.getCenter().opposite();
		final NearestNormal result;
		
		if ( Geometry.containsAngle( this.angleInterval, this.circumference.getCenter().opposite().getAngle()) ) {
			final float centerLength = this.circumference.getCenter().length();
			
			if ( centerLength > this.circumference.getRadius() ) {
				result = new NearestNormal(this.circumference.getCenter().opposite(), centerLength-this.circumference.getRadius());
			} else if ( ( centerLength == this.circumference.getRadius() ) || ( this.circumference.getCenter().equals(Vector2f.ZERO) ) ) {
				result = null;
			}
			else {
				result = new NearestNormal(this.circumference.getCenter(), this.circumference.getRadius()-centerLength);
			}
		}
		else {
			final Vector2f limitPoint;
			final float length;
			
			{
				final Vector2f limitPoint1 = this.getLimitPoint1();
				final Vector2f limitPoint2 = this.getLimitPoint2();
				final float length1 = limitPoint1.length();
				final float length2 = limitPoint2.length();
				
				if ( length1 < length2 ) {
					limitPoint = limitPoint1;
					length = length1;
				}
				else {
					limitPoint = limitPoint2;
					length = length2;
				}
			
			}
			
			Vector2f normal = limitPoint.sub(this.circumference.getCenter());
			
			if ( normal.scalarProjection(originRelativePosition) < this.circumference.getRadius() ) {
				normal = normal.opposite();
			}
			result = new NearestNormal(normal.normalise(), length);
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public CircumferenceSegment opposite() {
		return new CircumferenceSegment(this.circumference.opposite(), this.angleInterval.add((float) Math.PI));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public float minDistanceToOrigin() {
		final float result;
		
		if ( Geometry.containsAngle( this.angleInterval, this.circumference.getCenter().opposite().getAngle()) ) {
			result = Math.abs(this.circumference.getCenter().length()-this.circumference.getRadius());
		}
		else {
			result = Math.min(this.getLimitPoint1().length(), this.getLimitPoint2().length());
		}
		
		return result;
	}
	
}
