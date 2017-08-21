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
import com.esferixis.geometry.plane.Shape;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.QuadraticEquation;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class Circumference extends Curve<Circumference> {
	private static final long serialVersionUID = -1444586431472032881L;
	
	private final Vector2f center;
	private final float radius;
	
	/**
	 * @pre El centro no puede ser nulo
	 * @post Crea la circunsferencia con el centro y el radio especificados
	 */
	public Circumference(Vector2f center, float radius) {
		if ( center != null ) {
			if ( radius > 0 ) {
				this.center = center;
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
	 * @post Devuelve el centro
	 */
	public Vector2f getCenter() {
		return this.center;
	}
	
	/**
	 * @post Devuelve el radio
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/**
	 * @post Devuelve el punto correspondiente al ángulo especificado
	 */
	public Vector2f getPointWithAngle(float angle) {
		return this.center.add(Vector2f.getUnitVectorWithAngle(angle).scale(this.radius));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public Circumference transform(ProportionalMatrix3f matrix) {
		return new Circumference(matrix.transformPoint(this.center), matrix.transformScalar(this.radius));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public Circumference translate(Vector2f displacement) {
		return new Circumference(this.center.add(displacement), this.radius);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(Line rect) {
		final Vector2f origin_circumferenceCenter = rect.getReferencePoint().sub(this.getCenter());
		final Vector2f direction = rect.getDirection();
		
		return QuadraticEquation.resolve(direction.lengthSquared(), 2.0f * origin_circumferenceCenter.dot(direction), origin_circumferenceCenter.lengthSquared() - this.getRadius() * this.getRadius());
	}
	
	/**
	 * @pre La otra circunferencia no puede ser nula
	 * @post Devuelve los puntos de intersección de la circunferencia especificada
	 */
	protected List<Vector2f> getIntersections(Circumference other) {
		if ( other != null ) {
			/**
			 * Para una explicación geométrica ver:
			 * http://paulbourke.net/geometry/circlesphere/
			 * 
			 * También incluido en la documentación del proyecto
			 */
			
			final List<Vector2f> points = new ArrayList<Vector2f>(2);
			
			Vector2f centerDistance = this.getCenter().sub(other.getCenter());
			
			final float squareD = centerDistance.lengthSquared();
			
			if ( ( squareD == 0.0f ) && ( other.getRadius() == this.getRadius() ) ) {
				final Vector2f horizontalRadius = new Vector2f(other.getRadius(), 0.0f);
				
				// Infinitas soluciones, devuelve puntos arbitrarios
				points.add(other.getCenter().add(horizontalRadius));
				points.add(other.getCenter().sub(horizontalRadius));
			}
			else if ( ( squareD >= ExtraMath.square(other.getRadius() - this.getRadius())) && ( squareD <= ExtraMath.square(other.getRadius() + this.getRadius())) ) {
				final float d = (float) Math.sqrt(squareD);
				final float a = ( other.getRadius()*other.getRadius() - this.getRadius()*this.getRadius() + squareD ) / (2 * d);
				final float squareH = other.getRadius()*other.getRadius() - a * a;
				
				if ( squareH == 0.0f ) {
					points.add(centerDistance.normalise().scale(other.getRadius()).add(other.getCenter()));
				}
				else {
					final Vector2f p2 = centerDistance.normalise().scale(a).add(other.getCenter());
					final Vector2f d_p32 = centerDistance.rotate90AnticlockWise().scale((float) Math.sqrt(squareH) / d);
					
					points.add( p2.add(d_p32) );
					points.add( p2.sub(d_p32) );
				}
			}
			
			return Collections.unmodifiableList(points);
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
		return this.getCenter().hashCode() * 31 + Float.valueOf(this.radius).hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof Circumference) ) {
			final Circumference otherCircumference = (Circumference) other;
			return otherCircumference.getCenter().equals(this.getCenter()) && ( otherCircumference.getRadius() == this.getRadius() );
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
		return "Circumference( " + this.getCenter() + ", " + this.getRadius() + ")";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getPointWithAngle(0.0f);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return ( point.sub(this.getCenter()).lengthSquared() == this.getRadius() * this.getRadius() );
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Curve#getParametrization()
	 */
	@Override
	public com.esferixis.geometry.plane.finite.Curve.Parametrization getParametrization() {
		return new Parametrization(new FloatClosedInterval(0.0f, 2.0f * (float) Math.PI)) {

			@Override
			public Vector2f getPoint(float parameter) {
				return Circumference.this.getPointWithAngle(parameter);
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
		final Vector2f[] vertices = new Vector2f[]{
				this.center.add(new Vector2f(-this.radius, 0.0f)),
				this.center.add(new Vector2f(-this.radius, -this.radius)),
				this.center.add(new Vector2f(0.0f, -this.radius)),
				this.center.add(new Vector2f(this.radius, -this.radius)),
				this.center.add(new Vector2f(this.radius, 0.0f)),
				this.center.add(new Vector2f(this.radius, this.radius)),
				this.center.add(new Vector2f(0.0f, this.radius)),
				this.center.add(new Vector2f(-this.radius, this.radius))
		};
		
		final FiniteAffineHolomorphicShape<ConvexPolygon>[] boundingShapes = new FiniteAffineHolomorphicShape[vertices.length/2];
		
		int indexOffset = 0;
		for ( int i = 0 ; i<boundingShapes.length; i++ ) {
			final Vector2f vertex1 = vertices[indexOffset];
			final Vector2f vertex2 = vertices[indexOffset+1];
			final Vector2f vertex3 = vertices[ (i == boundingShapes.length-1 ) ? 0 : indexOffset+2 ];
			
			boundingShapes[i] = new ConvexPolygon(vertex1, vertex2, vertex3).castToAffine();
			indexOffset += 2;
		}
		
		return FiniteProportionalHolomorphicShapeGroup.castToAffine(new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends ConvexPolygon>>(boundingShapes));
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
			new Vector2f(this.center.getX()-this.radius, this.center.getY()-this.radius),
			new Vector2f(this.center.getX()+this.radius, this.center.getY()+this.radius)
		);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		return this.center.length() + this.radius;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public Circumference opposite() {
		return new Circumference(this.center.opposite(), this.radius);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		final Vector2f originRelativePosition = this.getCenter().opposite();
		final float originRelativePositionLength = originRelativePosition.length();
		final Shape.NearestNormal nearestNormal;
		
		if ( originRelativePositionLength >= this.radius ) {
			nearestNormal = new NearestNormal(originRelativePosition, originRelativePosition.length()-this.radius);
		} else if ( ( originRelativePositionLength == this.radius ) || ( this.center.equals(Vector2f.ZERO) ) ) {
			nearestNormal = null;
		}
		else {
			nearestNormal = new NearestNormal(this.center, this.radius-originRelativePosition.length());
		}
		
		return nearestNormal;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public float minDistanceToOrigin() {		
		return Math.abs(this.radius-this.getCenter().length());
	}
}
