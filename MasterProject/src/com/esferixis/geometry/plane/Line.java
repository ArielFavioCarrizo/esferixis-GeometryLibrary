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
package com.esferixis.geometry.plane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class Line extends Shape<Line> implements Serializable {
	private static final long serialVersionUID = 217586885876936473L;
	
	private final Vector2f referencePoint, direction;
	
	/**
	 * @post Crea la recta con el punto de referencia y el vector director
	 * 		 especificado
	 */
	public Line(Vector2f referencePoint, Vector2f direction) {
		if ( ( referencePoint != null ) && ( direction != null ) ) {
			this.referencePoint = referencePoint;
			this.direction = direction;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el punto de referencia
	 */
	public Vector2f getReferencePoint() {
		return this.referencePoint;
	}
	
	/**
	 * @post Devuelve el vector director
	 */
	public Vector2f getDirection() {
		return this.direction;
	}
	
	/**
	 * @post Devuelve la normal (Rotada a 90°)
	 */
	public Vector2f getNormal() {
		return this.direction.rotate90AnticlockWise();
	}
	
	/**
	 * @post Dado el escalar proporcional especificado, obtiene el punto
	 */
	public Vector2f getPointByProportionalScalar(float scalar) {
		return this.referencePoint.add(this.direction.scale(scalar));
	}
	
	/*
	 * @post Devuelve la proyección escalar de la intersección con la recta
	 * 		 especificada.
	 * 		 Si no hay intersección devuelve null
	 */
	/*
	public Float getIntersectionScalarProjection(Line other) {
		if ( other != null ) {
			float divisor = ( other.getNormal().dot(this.getDirection()) );
			
			return divisor != 0.0f ? other.getNormal().dot(other.getReferencePoint().sub(this.getReferencePoint())) / divisor : null;
		}
		else {
			throw new NullPointerException();
		}
	}
	*/
	
	/**
	 * @pre El punto no puede ser nulo
	 * @post Devuelve la distancia con el punto especificado, en términos de la
	 * 		 longitud del vector normal.
	 * 		 Positivo si es del lado de la normal, negativo en caso contrario.
	 */
	public float getScaledDistance(Vector2f point) {
		if ( point != null ) {
			return this.getNormal().dot( point.sub(this.referencePoint) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El punto no puede ser nulo
	 * @post Devuelve la distancia con el punto especificado
	 * 		 Positivo si es del lado de la normal, negativo en caso contrario.
	 */
	public float getDistance(Vector2f point) {
		return this.getScaledDistance(point) / (float) Math.sqrt( this.getNormal().lengthSquared() );
	}
	
	/**
	 * @pre El punto no puede ser nulo
	 * @post Devuelve el cuadrado de la distancia con el punto especificado
	 */
	public float getSquaredDistance(Vector2f point) {
		final float scaledDistance = this.getScaledDistance(point);
		return scaledDistance * scaledDistance / this.getNormal().lengthSquared();
	}

	/**
	 * @pre El desplazamiento no puede ser nulo
	 * @post Traslada la línea con el desplazamiento especificado
	 * @return
	 */
	public Line translate(Vector2f displacement) {
		if ( displacement != null ) {
			return new Line(this.getReferencePoint().add(displacement), this.getDirection());
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @pre La matriz no puede ser nula
	 * @post Transforma la línea con la matriz especificada
	 */
	public Line transform(Matrix3f matrix) {
		if ( matrix != null ) {
			return new Line(matrix.transformPoint( this.getReferencePoint() ), matrix.transformDirection( this.getDirection() ));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la intersección con la recta especificada
	 */
	public Float getRectIntersectionPoint(Line other) {
		if ( other != null ) {
			Float result = null;
			
			float divisor = ( this.getNormal().dot(other.getDirection()) );
			
			if ( divisor != 0.0f ) {
				result = this.getNormal().dot(this.getReferencePoint().sub(other.getReferencePoint())) / divisor;
			}
				
			return result;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La recta no puede ser nula
	 * @post Calcula la intersección de la recta especificada con ésta
	 * @param other
	 * @return
	 */
	public List<Float> getRectIntersection(Line other) {
		if ( other != null ) {
			List<Float> result = new ArrayList<Float>(1);
			
			Float resultPoint = this.getRectIntersectionPoint(other);
			
			if ( resultPoint != null ) {
				result.add(resultPoint);
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
		return this.getReferencePoint().hashCode() + this.getDirection().hashCode() * 31;
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof Line ) ) {
			final Line otherRect = (Line) other;
			
			return otherRect.getReferencePoint().equals(this.getReferencePoint()) && otherRect.getDirection().equals(this.getReferencePoint());
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
		return "Line( " + this.getReferencePoint() + ", " + this.getDirection() + " )";
	}

	/**
	 * @post Devuelve si contiene el punto especificado
	 * @return
	 */
	public boolean contains(Vector2f point) {
		return (this.getScaledDistance(point) == 0.0f);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public Line transform(ProportionalMatrix3f matrix) {
		return this.transform((Matrix3f) matrix);
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
	public Line opposite() {
		return new Line(this.referencePoint.opposite(), this.direction.opposite());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		float distance = this.getDistance(Vector2f.ZERO);
		final Vector2f normal;
		
		if ( distance >= 0.0f ) {
			normal = this.getDirection().rotate90AnticlockWise();
		}
		else {
			normal = this.getDirection().rotate90ClockWise();
			distance = -distance;
		}
		
		return new NearestNormal(normal, distance );
	}
}
