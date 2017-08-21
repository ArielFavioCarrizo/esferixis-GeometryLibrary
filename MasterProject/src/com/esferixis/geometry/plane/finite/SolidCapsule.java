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

import java.util.List;

import com.esferixis.geometry.plane.Line;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.collection.list.BinaryList;

/**
 * @author ariel
 *
 */
public final class SolidCapsule extends ClosedSurface<SolidCapsule> {
	private static final long serialVersionUID = 7853250867019612656L;
	
	private final LineSegment centerLine;
	private final float radius;
	
	/**
	 * @pre La línea central no puede ser nula
	 * @post Crea la cápsula con la línea central y el radio especificados
	 */
	public SolidCapsule(LineSegment centerLine, float radius) {
		if ( centerLine != null ) {
			if ( radius > 0 ) {
				this.centerLine = centerLine;
				this.radius = radius;
			}
			else {
				throw new IllegalArgumentException("Illegal radius");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la línea central
	 */
	public LineSegment getCenterLine() {
		return this.centerLine;
	}
	
	/**
	 * @post Devuelve el radio
	 */
	public float getRadius() {
		return this.radius;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public SolidCapsule transform(ProportionalMatrix3f matrix) {
		return new SolidCapsule(this.centerLine.transform(matrix), matrix.transformScalar(this.radius));
	}
	
	/**
	 * @post Devuelve los segmentos de circumferencia asociados a los puntos,	
	 * 		 en el orden correspondiente a los puntos.
	 */
	public BinaryList<CircumferenceSegment> getPerimeterCircumferenceSegments() {
		final Vector2f lineVector = this.centerLine.getPoint2().sub(this.centerLine.getPoint1());
		
		final float lineAngle = lineVector.getAngle();
		
		return new BinaryList<CircumferenceSegment>(
			new CircumferenceSegment(new Circumference(this.centerLine.getPoint1(), this.radius), new FloatClosedInterval(lineAngle + (float) Math.PI * 0.5f, lineAngle + (float) Math.PI * 1.5f )),
			new CircumferenceSegment(new Circumference(this.centerLine.getPoint2(), this.radius), new FloatClosedInterval(lineAngle - (float) Math.PI * 0.5f, lineAngle + (float) Math.PI * 0.5f ))
		);
	}
	
	/**
	 * @post Descompone la cápsula con dos líneas y dos arcos
	 */
	public FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>> getPerimeter() {
		final Vector2f lineVector = this.centerLine.getPoint2().sub(this.centerLine.getPoint1());
		final Vector2f displacement = lineVector.rotate90AnticlockWise().normalise().scale(this.radius);
		final float lineAngle = lineVector.getAngle();
		
		return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(
			new CircumferenceSegment(new Circumference(this.centerLine.getPoint1(), this.radius), new FloatClosedInterval(lineAngle + (float) Math.PI * 0.5f, lineAngle + (float) Math.PI * 1.5f )),
			new LineSegment(this.getCenterLine().getPoint1().sub(displacement), this.getCenterLine().getPoint2().sub(displacement)),
			new CircumferenceSegment(new Circumference(this.centerLine.getPoint2(), this.radius), new FloatClosedInterval(lineAngle - (float) Math.PI * 0.5f, lineAngle + (float) Math.PI * 0.5f )),
			new LineSegment(this.getCenterLine().getPoint2().add(displacement), this.getCenterLine().getPoint1().add(displacement))
		);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public SolidCapsule translate(Vector2f displacement) {
		return new SolidCapsule(this.centerLine.translate(displacement), this.radius);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRayIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public Float getRayIntersection(Line rect) {
		final Float t;
		
		if ( !this.hasIntersection(new Point(rect.getReferencePoint())) ) {
			t = this.getPerimeter().getRayIntersection(rect);
		}
		else {
			t = 0.0f;
		}
		
		return t;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(Line rect) {
		return this.getPerimeter().getRectIntersection(rect);
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getCenterLine().hashCode() * 31 + Float.valueOf(this.getRadius()).hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof SolidCapsule ) ) {
			final SolidCapsule otherPerimetralCapsule = (SolidCapsule) other;
			
			return otherPerimetralCapsule.getCenterLine().equals(this.getCenterLine()) && ( otherPerimetralCapsule.getRadius() == this.getRadius() );
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
		return "PerimetralCapsule( " + this.getCenterLine() + ", " + this.getRadius() + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getCenterLine().getInnerPoint();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {		
		final Vector2f v21 = this.getCenterLine().getVector12();
		final Vector2f p_v21 = v21.rotate90AnticlockWise().normalise();
		final Vector2f vp1 = point.sub(this.getCenterLine().getPoint1());
		
		boolean result = false;
		
		float distanceToCenterLine = Math.abs(p_v21.scalarProjection(vp1));
		float v21_vp1_projection = v21.scalarProjection(vp1);
		
		final float v21_length = (float) Math.sqrt( v21.lengthSquared() );
		
		if ( ( distanceToCenterLine <= this.getRadius() ) && ( ( v21_vp1_projection >= 0 ) && ( v21_vp1_projection <= v21_length ) ) ) {
			result = true;
		}
		else if ( distanceToCenterLine < this.getRadius() ) {
			if ( v21_vp1_projection < 0 ) {
				result = new Circle( new Circumference(Vector2f.ZERO, this.getRadius())).contains(vp1);
			}
			else if ( v21_vp1_projection > v21_length ) {
				result = new Circle( new Circumference(v21, this.getRadius()) ).contains(vp1);
			}
		}
		
		return result;
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
		final Vector2f v1 = this.getCenterLine().getPoint1();
		final Vector2f v2 = this.getCenterLine().getPoint2();
		
		final Vector2f v21 = this.getCenterLine().getVector12();
		final Vector2f deltaY = v21.rotate90AnticlockWise().normalise().scale(this.getRadius());
		final Vector2f deltaX = v21.normalise().scale(this.getRadius());
		
		return new ConvexPolygon(
				v1.sub(deltaY).sub(deltaX), v2.sub(deltaY).add(deltaX),
				v2.add(deltaY).add(deltaX), v1.add(deltaY).sub(deltaX)
		).castToAffine();
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
		BoundingBox lineSegmentBoundingBox = this.centerLine.boundingBox();
		Vector2f delta = new Vector2f(this.radius, this.radius);
		return new BoundingBox(lineSegmentBoundingBox.getVertex11().sub(delta), lineSegmentBoundingBox.getVertex22().add(delta));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public SolidCapsule opposite() {
		return new SolidCapsule(this.centerLine.opposite(), this.radius);
	}
}
