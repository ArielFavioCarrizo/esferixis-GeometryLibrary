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
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
public final class Circle extends ClosedSurface<Circle> {
	private static final long serialVersionUID = -2267851878495621657L;
	
	private final Circumference circumference;
	
	/**
	 * @pre La circunferencia no puede ser nula
	 * @post Crea el círculo con la circunferencia especificada
	 */
	public Circle(Circumference circumference) {
		if ( circumference != null ) {
			this.circumference = circumference;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la circunsferencia
	 */
	@Override
	public Circumference getPerimeter() {
		return this.circumference;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public Circle transform(ProportionalMatrix3f matrix) {
		return new Circle(this.circumference.transform(matrix));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public Circle translate(Vector2f displacement) {
		return new Circle(this.circumference.translate(displacement));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public List<Float> getRectIntersection(Line rect) {
		return this.circumference.getRectIntersection(rect);
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getPerimeter().hashCode() * 31 + 873268;
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( other != null ) {
			Circle otherCircle = (Circle) other;
			return otherCircle.getPerimeter().equals(this.getPerimeter());
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
		return "Circle( " + this.getPerimeter() + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.circumference.getCenter();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ClosedSurface#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return ( point.sub(this.getPerimeter().getCenter()).lengthSquared() <= this.getPerimeter().getRadius() * this.getPerimeter().getRadius());
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
		final Vector2f center = this.circumference.getCenter();
		final float radius = this.circumference.getRadius();
		return new ConvexPolygon(
			center.sub(new Vector2f(-radius, -radius)), center.sub(new Vector2f(radius, -radius)),
			center.sub(new Vector2f(radius, radius)), center.sub(new Vector2f(-radius, radius))
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
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public Circle opposite() {
		return new Circle(this.circumference.opposite());
	}
}
