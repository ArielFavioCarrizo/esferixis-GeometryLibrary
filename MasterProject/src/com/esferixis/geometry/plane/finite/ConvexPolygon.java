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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.esferixis.geometry.plane.Line;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.StringExtra;

/**
 * @author ariel
 *
 */
public final class ConvexPolygon extends ClosedSurface<ConvexPolygon> implements FiniteAffineHolomorphicShape.Casteable<ConvexPolygon> {
	private static final long serialVersionUID = 1174366073363133878L;
	
	private final List<Vector2f> vertices;
	
	/**
	 * @pre Los vértices tienen que formar un polígono convexo, y tienen
	 * 		que estar en sentido antihorario (Sentido positivo).
	 * 		Tiene que haber por lo menos 3 puntos
	 * 		Ninguno de los vértices puede ser nulo
	 * @post Crea el polígono convexo con los puntos especificados
	 */
	public ConvexPolygon(Collection<Vector2f> vertices) {
		this(vertices.toArray(new Vector2f[0]));
	}
	
	/**
	 * @pre Los vértices tienen que formar un polígono convexo, y tienen
	 * 		que estar en sentido horario (Sentido positivo).
	 * 		Tiene que haber por lo menos 3 puntos
	 * 		Ninguno de los vértices puede ser nulo
	 * @post Crea el polígono convexo con los puntos especificados
	 */
	public ConvexPolygon(Vector2f... vertices) {
		if ( vertices != null ) {
			vertices = vertices.clone();
			
			for ( Vector2f eachVertex : vertices ) {
				if ( eachVertex == null ) {
					throw new NullPointerException();
				}
			}
			
			if ( vertices.length >= 3 ) {
				this.vertices = Collections.unmodifiableList( Arrays.asList( vertices ) );
			}
			else {
				throw new IllegalArgumentException("Must be 3 vertices at least");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve los vértices (Sólo lectura)
	 */
	public List<Vector2f> getVertices() {
		return this.vertices;
	}
	
	/**
	 * @post Devuelve el centro
	 */
	public Vector2f getCenter() {
		Vector2f total = Vector2f.ZERO;
		
		for ( Vector2f eachVertex : this.vertices ) {
			total = total.add(eachVertex);
		}
		
		return total.scale(1.0f / (float) this.vertices.size());
	}
	
	/**
	 * @post Devuelve el iterador de líneas perimetrales
	 */
	public Iterator<LineSegment> getPerimetralLinesIterator() {
		return new Iterator<LineSegment>() {
			private int index = 0;
			private final List<Vector2f> vertices = ConvexPolygon.this.getVertices();

			@Override
			public boolean hasNext() {
				return this.index < this.vertices.size();
			}

			@Override
			public LineSegment next() {
				if ( this.hasNext() ) {
					final int oldIndex = this.index++;
					return new LineSegment( this.vertices.get(oldIndex), this.vertices.get( (oldIndex + 1) % this.vertices.size() ) );
				}
				else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	
	/**
	 * @post Devuelve las líneas perimetrales (Sólo lectura)
	 */
	public List<LineSegment> getPerimetralLines() {
		List< LineSegment > resultLines = new ArrayList<LineSegment>(this.getVertices().size());
		
		for ( int i = 0 ; i<this.getVertices().size() ; i++ ) {
			final Vector2f point1 = this.getVertices().get(i);
			final Vector2f point2 = this.getVertices().get( (i+1) % this.getVertices().size() );
			
			resultLines.add( new LineSegment(point1, point2) );
		}
		
		return Collections.unmodifiableList(resultLines);
	}
	
	/**
	 * @post Devuelve el perímetro
	 */
	public FiniteProportionalHolomorphicShape<?> getPerimeter() {
		return new FiniteProportionalHolomorphicShapeGroup<LineSegment>(this.getPerimetralLines());
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ClosedSurface#contains(com.arielcarrizo.math.Vector2f)
	 */
	public boolean contains(Vector2f point) {
		final Iterator<LineSegment> perimetralLinesIterator = this.getPerimetralLinesIterator();
				
		boolean intersects = true;
		
		while ( perimetralLinesIterator.hasNext() && intersects ) {
			LineSegment eachLine = perimetralLinesIterator.next();
			
			intersects = ( eachLine.getRect().getScaledDistance(point) >= 0.0f );
		}
		
		return intersects;
	}
	
	/**
	 * @post Devuelve la distancia con el punto especificado.
	 * 		 Positivo si no lo contiene, cero en caso contrario
	 */
	public float getDistance(Vector2f point) {
		float distance = Float.MAX_VALUE;
		
		final Iterator<LineSegment> perimetralLinesIterator = this.getPerimetralLinesIterator();
		
		boolean contains = true;
		
		while ( perimetralLinesIterator.hasNext() ) {
			final Line eachRect = perimetralLinesIterator.next().getRect();
			final float eachDistance = -eachRect.getDistance(point);
			
			if ( eachDistance >= 0.0f ) {
			
				if ( eachDistance < distance ) {
					distance = eachDistance;
				}
			
				contains = false;
			}
		}
		
		if ( contains ) {
			distance = 0.0f;
		}
		
		return distance;
	}
	
	/**
	 * @pre La recta no tiene que intersecar el polígono, y no puede ser nula
	 * @post Devuelve la proyección escalar del punto más cercano,
	 * 		 si la recta interseca el polígono, el resultado es indeterminado.
	 */
	/*
	public float getNearestPointScalarProjection(Line rect) {
		if ( rect != null ) {
			float nearestPointScalarProjection = 0.0f;
			float lastDistance = Float.MAX_VALUE;
			
			for ( Vector2f eachVertex : this.vertices ) {
				final float eachDistance = Math.abs( rect.getScaledDistance(eachVertex) );
				
				if ( eachDistance < lastDistance ) {
					nearestPointScalarProjection = rect.getDirection().scalarProjection(eachVertex.sub(rect.getReferencePoint()));;
				}
			}
			
			return nearestPointScalarProjection;
		}
		else {
			throw new NullPointerException();
		}
	}
	*/

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public ConvexPolygon translate(Vector2f displacement) {
		if ( displacement != null ) {
			List<Vector2f> resultVertices = new ArrayList<Vector2f>(this.vertices.size());
			
			for ( Vector2f eachVertex : this.vertices ) {
				resultVertices.add(eachVertex.add(displacement));
			}
			
			return new ConvexPolygon(this.vertices);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public ConvexPolygon transform(ProportionalMatrix3f matrix) {
		if ( matrix != null ) {
			List<Vector2f> resultVertices = new ArrayList<Vector2f>(this.vertices.size());
			
			for ( Vector2f eachVertex : this.vertices ) {
				resultVertices.add(matrix.transformPoint(eachVertex));
			}
			
			return new ConvexPolygon(resultVertices);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRayIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public Float getRayIntersection(Line rect) {
		if ( rect != null ) {
			Float t;
			if ( !this.hasIntersection( new Point(rect.getReferencePoint() )  ) ) {
				t = this.getPerimeter().getRayIntersection(rect);
			}
			else {
				t = 0.0f;
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
		return this.getPerimeter().getRectIntersection(rect);
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getVertices().hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof ConvexPolygon ) ) {
			final ConvexPolygon otherConvexPolygon = (ConvexPolygon) other;
			
			return otherConvexPolygon.getVertices().equals(this.getVertices());
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
		return "ConvexPolygon( " + StringExtra.join(", ", this.getVertices()) + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getCenter();
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
	public FiniteAffineHolomorphicShape<ConvexPolygon> castToAffine() {
		return new FiniteAffineHolomorphicShape<ConvexPolygon>(this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8253144882852023897L;

			@Override
			protected FiniteAffineHolomorphicShape<ConvexPolygon> castToAffine(ConvexPolygon shape) {
				return shape.castToAffine();
			}

			@Override
			protected ConvexPolygon transform_backingShape(Matrix3f matrix) {
				List<Vector2f> resultVertices = new ArrayList<Vector2f>(ConvexPolygon.this.vertices.size());
					
				for ( Vector2f eachVertex : ConvexPolygon.this.vertices ) {
					resultVertices.add(matrix.transformPoint(eachVertex));
				}
					
				return new ConvexPolygon(resultVertices);
			}

			@Override
			public <V, T extends Throwable> V accept(
					com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape.Visitor<V, T> visitor)
					throws T {
				return visitor.visitConvexPolygon(this);
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
		float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
		
		for ( Vector2f eachVertex : this.vertices ) {
			minX = Math.min(minX, eachVertex.getX());
			maxX = Math.min(maxX, eachVertex.getX());
			
			minY = Math.max(minY, eachVertex.getY());
			maxY = Math.min(maxY, eachVertex.getY());
		}
			
		return new BoundingBox(new Vector2f(minX, minY), new Vector2f(minY, maxY));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		float result = 0.0f;
		
		for ( Vector2f eachVertex : this.vertices ) {
			result = Math.max(result, eachVertex.length());
		}

		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public ConvexPolygon opposite() {
		Vector2f[] resultVertices = new Vector2f[this.vertices.size()];
		int i = this.vertices.size()-1;
		
		for ( Vector2f eachVertex : this.vertices ) {
			resultVertices[i--] = eachVertex.opposite();
		}
		
		return new ConvexPolygon(resultVertices);
	}
	
}
