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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esferixis.geometry.plane.Line;
import com.esferixis.geometry.plane.Shape.NearestNormal;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.StringExtra;

/**
 * @author ariel
 *
 */
public final class FiniteProportionalHolomorphicShapeGroup<S extends FiniteProportionalHolomorphicShape<?>> extends FiniteProportionalHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<S>> {
	private static final long serialVersionUID = 256877205253574878L;
	
	private final List<S> shapes;
	
	/**
	 * @pre La colección de figuras no puede ser nula, y
	 * 		ninguna de ellas pueden ser nulas
	 * @post Crea el grupo con las figuras especificadas
	 */
	public FiniteProportionalHolomorphicShapeGroup(Collection< ? extends S > shapes) {
		if ( shapes != null ) {
			this.shapes = Collections.unmodifiableList(new ArrayList<S>(shapes));
			
			for ( S eachShape : this.shapes ) {
				if ( eachShape == null ) {
					throw new NullPointerException();
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El array de figuras no puede ser nulo, y
	 * 		ninguna de ellas pueden ser nulas
	 * @post Crea el grupo con las figuras especificadas
	 */
	public FiniteProportionalHolomorphicShapeGroup(S... shapes) {
		if ( shapes != null ) {
			this.shapes = Collections.unmodifiableList( Arrays.asList(shapes.clone()) );
			
			for ( S eachShape : this.shapes ) {
				if ( eachShape == null ) {
					throw new NullPointerException();
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El grupo no puede ser nulo
	 * @post Castea a "affine"
	 */
	public static <S extends FiniteProportionalHolomorphicShape<?>> FiniteAffineHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>> castToAffine(final FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>> backingGroup) {
		if ( backingGroup != null ) {
			return new FiniteAffineHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>>(backingGroup) {
				/**
				 * 
				 */
				private static final long serialVersionUID = -6124853291706756053L;

				@Override
				protected FiniteAffineHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>> castToAffine(
						FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>> shape) {
					return castToAffine(shape);
				}

				@Override
				protected FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>> transform_backingShape(
						Matrix3f matrix) {
					FiniteAffineHolomorphicShape<? extends S>[] resultShapes = new FiniteAffineHolomorphicShape[this.getBackingShape().getShapes().size()];
					int i = 0;
					
					for ( FiniteAffineHolomorphicShape<? extends S> eachShape : this.getBackingShape().getShapes() ) {
						resultShapes[i++] = eachShape.transform(matrix);
					}
					
					return new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>(resultShapes);
				}

				@Override
				public <V, T extends Throwable> V accept(
						com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape.Visitor<V, T> visitor)
						throws T {
					return visitor.visitGroup(this);
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve las figuras
	 */
	public List<S> getShapes() {
		return this.shapes;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public FiniteProportionalHolomorphicShapeGroup<S> transform(ProportionalMatrix3f matrix) {
		if ( matrix != null ) {
			List<S> newShapes = new ArrayList<S>(this.shapes.size());
			
			for ( int i = 0 ; i < this.shapes.size() ; i++ ) {
				newShapes.add( (S) this.shapes.get(i).transform(matrix) );
			}
			
			return new FiniteProportionalHolomorphicShapeGroup<S>(newShapes);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public FiniteProportionalHolomorphicShapeGroup<S> translate(Vector2f displacement) {
		if ( displacement != null ) {
			List<S> newShapes = new ArrayList<S>(this.shapes.size());
			
			for ( int i = 0 ; i < this.shapes.size() ; i++ ) {
				newShapes.add( (S) this.shapes.get(i).translate(displacement) );
			}
			
			return new FiniteProportionalHolomorphicShapeGroup<S>(newShapes);
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
		List<Float> intersections = new ArrayList<Float>();
		
		for ( FiniteProportionalHolomorphicShape<?> eachShape : this.getShapes() ) {
			intersections.addAll( eachShape.getRectIntersection(rect) );
		}
		
		return Collections.unmodifiableList(intersections);
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRayIntersection(com.arielcarrizo.geometry.plane.Rect)
	 */
	@Override
	public Float getRayIntersection(Line rect) {		
		Float t = null;
		
		for ( FiniteProportionalHolomorphicShape<?> eachShape : this.getShapes() ) {
			final Float eachT = eachShape.getRayIntersection(rect);
			
			if ( ( t == null ) || ( ( eachT != null ) && ( eachT < t ) ) ) {
				t = eachT;
			}
		}
		
		return t;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getShapes().hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof FiniteProportionalHolomorphicShapeGroup ) ) {
			final FiniteProportionalHolomorphicShapeGroup<S> otherProportionalHolomorphicShapeGroup = (FiniteProportionalHolomorphicShapeGroup<S>) this;
				
			return ( other == this) || ( otherProportionalHolomorphicShapeGroup.getShapes().equals(this.getShapes()) );
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
		return "ProportionalHolomorphicShapeGroup( " + StringExtra.join(", ", this.getShapes()) + " )";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnestPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.getShapes().get(0).getInnerPoint();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		final Iterator<S> shapesIterator = this.getShapes().iterator();
		boolean containsPoint = false;
		
		while ( shapesIterator.hasNext() && (!containsPoint) ) {
			containsPoint = shapesIterator.next().contains(point);
		}
		
		return containsPoint;
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
		FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>>[] resultShapes = new FiniteAffineHolomorphicShape[this.shapes.size()];
		
		int i = 0;
		for ( FiniteProportionalHolomorphicShape<?> eachShape : this.shapes ) {
			resultShapes[i++] = eachShape.getBoundingAffineHolomorphicShape();
		}
		
		return castToAffine(new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>>>(resultShapes));
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
		BoundingBox boundingBox = null;
		
		for ( S eachShape : this.shapes ) {
			final BoundingBox eachBoundingBox = eachShape.boundingBox();
			
			if ( boundingBox == null ) {
				boundingBox = eachBoundingBox;
			}
			else {
				boundingBox = boundingBox.unionBoundingBox(eachBoundingBox);
			}
		}
		
		return boundingBox;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public float maxDistanceToOrigin() {
		float result = 0.0f;
		
		for ( S eachShape : this.shapes ) {
			result = Math.max(result, eachShape.maxDistanceToOrigin());
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public float minDistanceToOrigin() {
		float result = Float.POSITIVE_INFINITY;
		
		for ( S eachShape : this.shapes ) {
			result = Math.min(result, eachShape.minDistanceToOrigin());
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public FiniteProportionalHolomorphicShapeGroup<S> opposite() {
		List<S> newShapes = new ArrayList<S>(this.shapes.size());
		
		for ( int i = 0 ; i < this.shapes.size() ; i++ ) {
			newShapes.add( (S) this.shapes.get(i).opposite() );
		}
		
		return new FiniteProportionalHolomorphicShapeGroup<S>(newShapes);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		NearestNormal resultNormal = null;
		
		for ( S eachShape : this.shapes ) {
			final NearestNormal eachNormal = eachShape.nearestNormalToOrigin();
			
			if ( eachNormal != null ) {
				if ( ( resultNormal == null ) || ( eachNormal.getDistance() < resultNormal.getDistance() ) ) {
					resultNormal = eachNormal;
				}
			}
		}

		return resultNormal;
	}
}
