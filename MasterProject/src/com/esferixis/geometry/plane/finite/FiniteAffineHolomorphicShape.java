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
import java.util.List;

import com.esferixis.geometry.plane.Line;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.multimethod.Bimethod;
import com.esferixis.misc.multimethod.SymmetricBimethod;

/**
 * @author ariel
 *
 */
public abstract class FiniteAffineHolomorphicShape<S extends FiniteProportionalHolomorphicShape<S>> extends FiniteProportionalHolomorphicShape<FiniteAffineHolomorphicShape<S>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2813310521701054605L;
	
	private final S backingShape;
	
	public static interface Casteable<S extends FiniteProportionalHolomorphicShape<S>> {
		/**
		 * @post Castea a una figura holomórfica afín
		 */
		public FiniteAffineHolomorphicShape<S> castToAffine();
	}
	
	public interface Visitor<V, T extends Throwable> {
		/**
		 * @post Visita un punto
		 */
		public V visitPoint(FiniteAffineHolomorphicShape<Point> point) throws T;
		
		/**
		 * @post Visita un segmento
		 */
		public V visitLineSegment(FiniteAffineHolomorphicShape<LineSegment> lineSegment) throws T;

		/**
		 * @post Visita un polígono convexo
		 */
		public V visitConvexPolygon(FiniteAffineHolomorphicShape<ConvexPolygon> convexPolygon) throws T;
		
		/**
		 * @post Visita un grupo
		 */
		public <S extends FiniteProportionalHolomorphicShape<?>> V visitGroup(FiniteAffineHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>> group) throws T;
	}
	
	private static FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> uncheckedCastToAffine(FiniteProportionalHolomorphicShape<?> shape) {
		return shape.accept(new com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape.Visitor<FiniteAffineHolomorphicShape<?>, RuntimeException>() {

			@Override
			public FiniteAffineHolomorphicShape<?> visit(Point point) throws RuntimeException {
				return point.castToAffine();
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(LineSegment lineSegment) throws RuntimeException {
				return lineSegment.castToAffine();
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(Circumference circumference) throws RuntimeException {
				return null;
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(CircumferenceSegment circumferenceSegment) throws RuntimeException {
				return null;
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(Circle circle) throws RuntimeException {
				return null;
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(SolidCapsule capsule) throws RuntimeException {
				return null;
			}

			@Override
			public FiniteAffineHolomorphicShape<?> visit(ConvexPolygon convexPolygon) throws RuntimeException {
				return convexPolygon.castToAffine();
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<?>> FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> visit(
					FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup) throws RuntimeException {
				FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>>[] resultShapes = new FiniteAffineHolomorphicShape<?>[proportionalHolomorphicShapeGroup.getShapes().size()];
				int i = 0;
				for ( S eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
					resultShapes[i++] = cast(eachShape);
				}
				
				return FiniteProportionalHolomorphicShapeGroup.castToAffine(new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>>>(resultShapes));
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<S>> FiniteAffineHolomorphicShape<?> visit(
					FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
				return finiteAffineHolomorphicShape;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private static final SymmetricBimethod<Void, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>> dilationBimethod = SymmetricBimethod.make( (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class,
		new Bimethod.Case<Void, Point, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>(Point.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, Point point,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				return otherShape.translate(point.getPosition());
			}
			
		},
		new Bimethod.Case<Void, LineSegment, LineSegment, FiniteProportionalHolomorphicShape<?>>(LineSegment.class, LineSegment.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, LineSegment line1, LineSegment line2) {
				final Vector2f p11 = line1.getPoint1().add(line2.getPoint1());
				final Vector2f p12 = line1.getPoint1().add(line2.getPoint2());
				final Vector2f p21 = line1.getPoint2().add(line2.getPoint1());
				final Vector2f p22 = line1.getPoint2().add(line2.getPoint2());
				
				final Vector2f center = p11.add(p21).add(p22).add(p12).scale(1.0f / 4.0f);
				
				final Vector2f[] points;
				
				if ( new Line(p11, p21.sub(p11)).getScaledDistance(center) >= 0.0f ) {
					points = new Vector2f[]{p11, p21, p22, p12};
				}
				else {
					points = new Vector2f[]{p12, p22, p21, p11};
				}
				
				return new ConvexPolygon(points);
			}
			
		},
		new Bimethod.Case<Void, LineSegment, ConvexPolygon, FiniteProportionalHolomorphicShape<?>>(LineSegment.class, ConvexPolygon.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, LineSegment lineSegment,
					ConvexPolygon convexPolygon) {
				final int[] extremeVertices = new int[2];
				
				{
					float minDistance = Float.POSITIVE_INFINITY;
					float maxDistance = Float.NEGATIVE_INFINITY;
					
					for ( int i = 0 ; i<convexPolygon.getVertices().size() ; i++ ) {
						final Vector2f eachVertex = convexPolygon.getVertices().get(i);
						final float eachVertexDistance = lineSegment.getRect().getScaledDistance(eachVertex);
						
						if ( ( i == 0 ) || ( eachVertexDistance < minDistance ) ) {
							extremeVertices[0] = i;
							minDistance = eachVertexDistance;
						}
						
						if ( ( i == 0) || ( eachVertexDistance > maxDistance ) ) {
							extremeVertices[1] = i;
							maxDistance = eachVertexDistance;
						}
					}
				}
				
				Vector2f[] resultVertices = new Vector2f[convexPolygon.getVertices().size()+2];
				
				if ( extremeVertices[0] > extremeVertices[1] ) {
					lineSegment = lineSegment.oppositeOrder();
					
					int temp = extremeVertices[0];
					extremeVertices[0] = extremeVertices[1];
					extremeVertices[1] = temp;
				}
				
				Vector2f offset = lineSegment.getPoint1();
				
				int keyExtreme = 0;
				int resultIndex = 0;
				int i = 0;
				
				while ( i<convexPolygon.getVertices().size() ) {
					resultVertices[resultIndex++] = convexPolygon.getVertices().get( (i + extremeVertices[0]) % convexPolygon.getVertices().size() ).add(offset);
					
					if ( ( keyExtreme < 2 ) && ( i == extremeVertices[keyExtreme]-extremeVertices[0] ) ) {
						if ( keyExtreme != 0 ) {
							offset = lineSegment.getPoint1();
						}
						else {
							offset = lineSegment.getPoint2();
						}
						
						keyExtreme++;
					}
					else {
						i++;
					}
				}
				
				return new ConvexPolygon(resultVertices);
			}
			
		},
		new Bimethod.Case<Void, ConvexPolygon, ConvexPolygon, FiniteProportionalHolomorphicShape<?>>(ConvexPolygon.class, ConvexPolygon.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, ConvexPolygon convexPolygon1,
					ConvexPolygon convexPolygon2) {				
				final float baseAngle = convexPolygon1.getVertices().get(1).sub(convexPolygon1.getVertices().get(0)).getAngle();
				float[] edgeAngles1 = new float[convexPolygon1.getVertices().size()];
				float[] edgeAngles2 = new float[convexPolygon2.getVertices().size()];
				
				for ( int i = 0 ; i <convexPolygon1.getVertices().size() ; i++ ) {
					float angle = convexPolygon1.getVertices().get( (i+1) % convexPolygon1.getVertices().size()).sub(convexPolygon1.getVertices().get(i)).getAngle();
					
					if ( angle < baseAngle ) {
						angle += Math.PI * 2.0f;
					}
					
					edgeAngles1[i] = angle;
				}
				
				int indexOffset2 = 0;
				
				{
					float minAngle = Float.POSITIVE_INFINITY;
					
					for ( int i = 0 ; i <convexPolygon2.getVertices().size() ; i++ ) {
						float angle = convexPolygon2.getVertices().get( (i+1) % convexPolygon2.getVertices().size()).sub(convexPolygon2.getVertices().get(i)).getAngle();
						
						if ( angle < baseAngle ) {
							angle += Math.PI * 2.0f;
						}
						
						if ( angle < minAngle ) {
							minAngle = angle;
							indexOffset2 = i;
						}
						
						edgeAngles2[i] = angle;
					}
				}
				
				int index1 = 0, index2 = 0;
				
				Vector2f[] resultVertices = new Vector2f[convexPolygon1.getVertices().size()+convexPolygon2.getVertices().size()];
				int resultIndex = 0;
				
				boolean moreEdges1 = true, moreEdges2 = true;
				
				while ( moreEdges1 || moreEdges2 ) {
					final int absoluteIndex2 = (index2+indexOffset2) % convexPolygon2.getVertices().size();
					final int absoluteIndex1 = (index1) % convexPolygon1.getVertices().size();
					
					resultVertices[resultIndex++] = convexPolygon1.getVertices().get(absoluteIndex1).add(convexPolygon2.getVertices().get(absoluteIndex2));
					
					if ( moreEdges1 && ( !moreEdges2 || ( edgeAngles1[absoluteIndex1] < edgeAngles2[absoluteIndex2] ) ) ) {
						index1++;
					}
					else if ( moreEdges2 ) {
						index2++;
					}
					
					moreEdges1 = (index1 < convexPolygon1.getVertices().size());
					moreEdges2 = (index2 < convexPolygon2.getVertices().size());
				}
				
				return new ConvexPolygon(resultVertices);
			}
			
		},
		new Bimethod.Case<Void, FiniteProportionalHolomorphicShapeGroup<?>, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>((Class<FiniteProportionalHolomorphicShapeGroup<?>>) (Class<?>) FiniteProportionalHolomorphicShapeGroup.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, FiniteProportionalHolomorphicShapeGroup<?> proportionalHolomorphicShapeGroup,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				final List<FiniteProportionalHolomorphicShape<?>> shapes = new ArrayList<FiniteProportionalHolomorphicShape<?>>(proportionalHolomorphicShapeGroup.getShapes().size());
				
				for ( FiniteProportionalHolomorphicShape<?> eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
					shapes.add( dilationBimethod.process(null, eachShape, otherShape) );
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(shapes);
			}
			
		},
		new Bimethod.Case<Void, FiniteAffineHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>((Class<FiniteAffineHolomorphicShape<?>>) (Class<?>) FiniteAffineHolomorphicShape.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, FiniteAffineHolomorphicShape<?> finiteAffineHolomorphicShape, FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
				return dilationBimethod.process(null, finiteAffineHolomorphicShape.getBackingShape(), proportionalHolomorphicShape);
			}
			
		}
	);
	
	/**
	 * @post Crea una cobertura de la figura especificada
	 */
	FiniteAffineHolomorphicShape(S backingShape) {
		if ( backingShape != null ) {
			this.backingShape = backingShape;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Castea a affine
	 */
	protected abstract FiniteAffineHolomorphicShape<S> castToAffine(S shape);

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#translate(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public FiniteAffineHolomorphicShape<S> translate(Vector2f displacement) {
		return this.castToAffine(this.backingShape.translate(displacement));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#transform(com.arielcarrizo.math.ProportionalMatrix3f)
	 */
	@Override
	public FiniteAffineHolomorphicShape<S> transform(ProportionalMatrix3f matrix) {
		return this.castToAffine(this.backingShape.transform(matrix));
	}
	
	/**
	 * @pre La matriz no puede ser nula
	 * @post Transforma la figura con la matriz especificada
	 * @param point
	 * @return
	 */
	public final FiniteAffineHolomorphicShape<S> transform(Matrix3f matrix) {
		if ( matrix != null ) {
			return this.castToAffine(this.transform_backingShape(matrix));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Asegura que la matriz no es nula
	 * @post Transforma la figura con la matriz especificada
	 * @param point
	 * @return
	 */
	protected abstract S transform_backingShape(Matrix3f matrix);
	
	/**
	 * @post Devuelve la figura representada
	 */
	public final S getBackingShape() {
		return this.backingShape;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#contains(com.arielcarrizo.math.Vector2f)
	 */
	@Override
	public boolean contains(Vector2f point) {
		return this.backingShape.contains(point);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getInnerPoint()
	 */
	@Override
	public Vector2f getInnerPoint() {
		return this.backingShape.getInnerPoint();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getRectIntersection(com.arielcarrizo.geometry.plane.Line)
	 */
	@Override
	public List<Float> getRectIntersection(Line rect) {
		return this.backingShape.getRectIntersection(rect);
	}
	
	/**
	 * @pre La figura no puede ser nula
	 * @post Devuelve el resultado perimetral de la dilatación con la figura especificada
	 */
	public final FiniteAffineHolomorphicShape<?> perimetralDilate(final FiniteAffineHolomorphicShape<?> other) {
		if ( other != null ) {
			return uncheckedCastToAffine(FiniteAffineHolomorphicShape.this.getBackingShape().perimetralDilate(other.getBackingShape()));
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
		return this.backingShape.hashCode() + 42921;
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof FiniteAffineHolomorphicShape<?> ) ) {
			final FiniteAffineHolomorphicShape<?> otherAffine = (FiniteAffineHolomorphicShape<?>) other;
			
			return otherAffine.getBackingShape().equals(this.getBackingShape());
		}
		else {
			return false;
		}
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
	 * @see com.arielcarrizo.geometry.plane.Shape#accept(com.arielcarrizo.geometry.plane.Shape.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(com.esferixis.geometry.plane.Shape.Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
	
	/**
	 * @post Procesa con el visitor especificado y devuelve el resultado
	 */
	public abstract <V, T extends Throwable> V accept(final Visitor<V,T> affineVisitor) throws T;
	
	/**
	 * @pre La figura especificada no puede ser nula
	 * @post Efectúa la dilatación con la figura especificada
	 */
	public FiniteAffineHolomorphicShape<?> dilate(FiniteAffineHolomorphicShape<?> otherShape) {
		if ( otherShape != null ) {
			return cast(dilationBimethod.process(null, this.getBackingShape(), otherShape.getBackingShape()));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La figura no puede ser nula
	 * @post Devuelve si es casteable la figura especificada
	 */
	public static boolean isCasteable(FiniteProportionalHolomorphicShape<?> originalShape) {
		if ( originalShape != null ) {
			return ( uncheckedCastToAffine(originalShape) != null );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La figura no puede ser nula, y tiene que ser casteable
	 * @post Castea la figura especificada
	 */
	public static FiniteAffineHolomorphicShape<?> cast(FiniteProportionalHolomorphicShape<?> originalShape) {
		if ( originalShape != null ) {
			FiniteAffineHolomorphicShape<?> resultShape = uncheckedCastToAffine(originalShape);
			
			if ( resultShape == null ) {
				throw new IllegalArgumentException("Invalid shape");
			}
			
			return resultShape;
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape#getBoundingAffineHolomorphicShape()
	 */
	@Override
	public final FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> getBoundingAffineHolomorphicShape() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getBoundingBox()
	 */
	@Override
	public BoundingBox boundingBox() {
		return this.backingShape.boundingBox();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#getMaxDistanceToOrigin()
	 */
	@Override
	public final float maxDistanceToOrigin() {
		return this.backingShape.maxDistanceToOrigin();
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape#minDistanceToOrigin()
	 */
	@Override
	public final float minDistanceToOrigin() {
		return this.backingShape.minDistanceToOrigin();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#opposite()
	 */
	@Override
	public FiniteAffineHolomorphicShape<S> opposite() {
		return this.castToAffine(this.backingShape.opposite());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.geometry.plane.Shape#getNearestNormalToOrigin()
	 */
	@Override
	public com.esferixis.geometry.plane.Shape.NearestNormal nearestNormalToOrigin() {
		return this.backingShape.nearestNormalToOrigin();
	}
}
