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
import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import com.esferixis.geometry.Geometry;
import com.esferixis.geometry.plane.Line;
import com.esferixis.geometry.plane.Shape;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.classextra.ConcreteInstanceClassIndexer;
import com.esferixis.misc.multimethod.Bimethod;
import com.esferixis.misc.multimethod.SymmetricBimethod;

/**
 * @author ariel
 *
 */
public abstract class FiniteProportionalHolomorphicShape<S extends FiniteProportionalHolomorphicShape<S>> extends Shape<S> implements Serializable {
	private static final long serialVersionUID = 1781634333091427547L;

	public interface Visitor<V, T extends Throwable> {
		public V visit(Point point) throws T;
		public V visit(LineSegment lineSegment) throws T;
		public V visit(Circumference circumference) throws T;
		public V visit(CircumferenceSegment circumferenceSegment) throws T;
		public V visit(Circle circle) throws T;
		public V visit(SolidCapsule capsule) throws T;
		public V visit(ConvexPolygon convexPolygon) throws T;
		public <S extends FiniteProportionalHolomorphicShape<S>> V visit(FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws T;
		public <S extends FiniteProportionalHolomorphicShape<?>> V visit(FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup) throws T;
	}
	
	@SuppressWarnings("unchecked")
	public static final ConcreteInstanceClassIndexer<FiniteProportionalHolomorphicShape<?>> CONCRETEINSTANCECLASSINDEXER = new ConcreteInstanceClassIndexer<FiniteProportionalHolomorphicShape<?>>( (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class, 9) {

		@Override
		public int classIndex(FiniteProportionalHolomorphicShape<?> shape) {
			return shape.accept(new Visitor<Integer, RuntimeException>() {

				@Override
				public Integer visit(Point point) {
					return 0;
				}

				@Override
				public Integer visit(LineSegment line) {
					return 1;
				}

				@Override
				public Integer visit(Circumference circumference) {
					return 2;
				}

				@Override
				public Integer visit(Circle circle) {
					return 3;
				}

				@Override
				public Integer visit(SolidCapsule capsule) {
					return 4;
				}

				@Override
				public Integer visit(CircumferenceSegment circumferenceSegment) {
					return 5;
				}

				@Override
				public Integer visit(ConvexPolygon convexPolygon) {
					return 6;
				}

				@Override
				public <S extends FiniteProportionalHolomorphicShape<?>> Integer visit(
						FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup)
						throws RuntimeException {
					return 7;
				}

				@Override
				public <S extends FiniteProportionalHolomorphicShape<S>> Integer visit(
						FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
					return 8;
				}
				
			});
		}
		
	};
	
	@SuppressWarnings("unchecked")
	private static final SymmetricBimethod<Void, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>> perimetralDilationBimethod = SymmetricBimethod.make( (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class,
		new Bimethod.Case<Void, Point, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>(Point.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, Point point,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				return otherShape.transform(Matrix3f.IDENTITY.translate(point.getPosition()));
			}
			
		},
		new Bimethod.Case<Void, LineSegment, LineSegment, FiniteProportionalHolomorphicShape<?>>(LineSegment.class, LineSegment.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, LineSegment line1, LineSegment line2) {
				final Vector2f p11 = line1.getPoint1().add(line2.getPoint1());
				final Vector2f p12 = line1.getPoint1().add(line2.getPoint2());
				final Vector2f p21 = line1.getPoint2().add(line2.getPoint1());
				final Vector2f p22 = line1.getPoint2().add(line2.getPoint2());
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(new LineSegment(p11, p21), new LineSegment(p12, p22), new LineSegment(p11, p12), new LineSegment(p21, p22));
			}
			
		},
		new Bimethod.Case<Void, LineSegment, Circumference, FiniteProportionalHolomorphicShape<?>>(LineSegment.class, Circumference.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, LineSegment lineSegment, Circumference circumference) {
				final float lineSegmentAngle = lineSegment.getVector12().getAngle();
				final float angle1 = (float) (lineSegmentAngle+Math.PI*0.5f);
				final float angle2 = (float) (lineSegmentAngle+Math.PI*1.5f);
				
				FiniteProportionalHolomorphicShape<?>[] resultShapes = new FiniteProportionalHolomorphicShape[4];
				resultShapes[0] = circumference.translate(lineSegment.getPoint1());
				resultShapes[1] = circumference.translate(lineSegment.getPoint2());
				resultShapes[2] = lineSegment.translate(circumference.getPointWithAngle(angle1));
				resultShapes[3] = lineSegment.translate(circumference.getPointWithAngle(angle2));
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(resultShapes);
				
				//return new SolidCapsule(line.transform(Matrix3f.IDENTITY.translate(circumference.getCenter())), circumference.getRadius()).getPerimeter();
			}
			
		},
		new Bimethod.Case<Void, LineSegment, CircumferenceSegment, FiniteProportionalHolomorphicShape<?>>(LineSegment.class, CircumferenceSegment.class) {
			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, LineSegment lineSegment, CircumferenceSegment circumferenceSegment) {
				final FiniteProportionalHolomorphicShape<?>[] shapesTranslatedByExtremes = new FiniteProportionalHolomorphicShape[]{
						circumferenceSegment.translate(lineSegment.getPoint1()), circumferenceSegment.translate(lineSegment.getPoint2()),
						lineSegment.translate(circumferenceSegment.getLimitPoint1()), lineSegment.translate(circumferenceSegment.getLimitPoint2())
				};
				
				final List<FiniteProportionalHolomorphicShape<?>> result = new ArrayList<FiniteProportionalHolomorphicShape<?>>(shapesTranslatedByExtremes.length+2);
				result.addAll(Arrays.asList(shapesTranslatedByExtremes));
				
				{
					final float lineSegmentAngle = lineSegment.getVector12().getAngle();
					final float angle1 = (float) (lineSegmentAngle+Math.PI*0.5f);
					final float angle2 = (float) (lineSegmentAngle+Math.PI*1.5f);
					
					if ( Geometry.containsAngle( circumferenceSegment.getAngleInterval(), angle1 ) ) {
						result.add(lineSegment.translate(circumferenceSegment.getCircumference().getPointWithAngle(angle1) ));
					}
					
					if ( Geometry.containsAngle( circumferenceSegment.getAngleInterval(), angle2 ) ) {
						result.add(lineSegment.translate(circumferenceSegment.getCircumference().getPointWithAngle(angle2) ));
					}
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(result);
			}
			
		},
		new Bimethod.Case<Void, Circumference, Circumference, FiniteProportionalHolomorphicShape<?>>(Circumference.class, Circumference.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, final Circumference circumference1,
					final Circumference circumference2) {
				final Circumference maxCircumference = new Circumference(circumference1.getCenter().add(circumference2.getCenter()), circumference1.getRadius() + circumference2.getRadius());
				
				final FiniteProportionalHolomorphicShape<?> result;
				
				if ( circumference1.getRadius() == circumference2.getRadius() ) {
					result = maxCircumference;
				}
				else {
					result = new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(maxCircumference, new Circumference(circumference1.getCenter().add(circumference2.getCenter()), Math.abs(circumference1.getRadius() - circumference2.getRadius())));
				}
				return result;
			}
			
		},
		new Bimethod.Case<Void, CircumferenceSegment, CircumferenceSegment, FiniteProportionalHolomorphicShape<?>>(CircumferenceSegment.class, CircumferenceSegment.class) {

			private float getAngleContact(float beta, boolean outsideCircumference) {
				final float resultAngle;
				
				if ( outsideCircumference ) {
					resultAngle = beta;
				}
				else {
					resultAngle = beta + (float) Math.PI;
				}
				
				return resultAngle;
			}
			
			private void makeJoints(List< FiniteProportionalHolomorphicShape<?> > shapes, Circumference dilationCircumference, boolean side, CircumferenceSegment circumferenceSegment1, CircumferenceSegment circumferenceSegment2) {
				float angleOffset2 = ( side ? 0.0f : (float) Math.PI );
				
				for ( FloatClosedInterval eachAngleInterval : Geometry.angleIntervalIntersection( circumferenceSegment1.getAngleInterval(), circumferenceSegment2.getAngleInterval().add(angleOffset2) ) ) {
					if ( !side && ( circumferenceSegment1.getCircumference().getRadius() > circumferenceSegment2.getCircumference().getRadius() ) ) {
						angleOffset2 = 0.0f;
					}
					
					shapes.add(new CircumferenceSegment( dilationCircumference, eachAngleInterval.sub(angleOffset2)) );
				}
			}
			
			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, CircumferenceSegment circumferenceSegment1,
					CircumferenceSegment circumferenceSegment2) {
				final List< FiniteProportionalHolomorphicShape<?> > shapes = new ArrayList< FiniteProportionalHolomorphicShape<?> >(5);
				
				final Vector2f dilationCenter = circumferenceSegment1.getCircumference().getCenter().add(circumferenceSegment2.getCircumference().getCenter());
				final float radius1 = circumferenceSegment1.getCircumference().getRadius();
				final float radius2 = circumferenceSegment2.getCircumference().getRadius();
				
				final CircumferenceSegment circumferenceSegment11 = circumferenceSegment1.translate(circumferenceSegment2.getLimitPoint1());
				final CircumferenceSegment circumferenceSegment12 = circumferenceSegment1.translate(circumferenceSegment2.getLimitPoint2());
				final CircumferenceSegment circumferenceSegment21 = circumferenceSegment2.translate(circumferenceSegment1.getLimitPoint1());
				final CircumferenceSegment circumferenceSegment22 = circumferenceSegment2.translate(circumferenceSegment1.getLimitPoint2());
				
				shapes.add(circumferenceSegment11);
				shapes.add(circumferenceSegment12);
				shapes.add(circumferenceSegment21);
				shapes.add(circumferenceSegment22);
				
				{
					final Circumference dilationOutsideCircumference = new Circumference( dilationCenter, radius1 + radius2 );
					
					makeJoints(shapes, dilationOutsideCircumference, true, circumferenceSegment1, circumferenceSegment2);
				}
				
				if ( circumferenceSegment1.getCircumference().getRadius() != circumferenceSegment2.getCircumference().getRadius() ) {
					final Circumference dilationInsideCircumference = new Circumference(dilationCenter, Math.abs( circumferenceSegment1.getCircumference().getRadius() - circumferenceSegment2.getCircumference().getRadius() ));
					
					makeJoints(shapes, dilationInsideCircumference, false, circumferenceSegment1, circumferenceSegment2);
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(shapes);
			}

		},
		new Bimethod.Case<Void, Circumference, CircumferenceSegment, FiniteProportionalHolomorphicShape<?>>(Circumference.class, CircumferenceSegment.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, final Circumference circumference,
					final CircumferenceSegment circumferenceSegment) {
				final List< FiniteProportionalHolomorphicShape<?> > shapes = new ArrayList< FiniteProportionalHolomorphicShape<?> >(4);
				
				final Vector2f dilationCenter = circumferenceSegment.getCircumference().getCenter().add(circumference.getCenter());
				
				shapes.add(circumference.translate(circumferenceSegment.getLimitPoint1()));
				shapes.add(circumference.translate(circumferenceSegment.getLimitPoint2()));
				
				if ( circumferenceSegment.getCircumference().getRadius() > circumference.getRadius() ) {
					shapes.add(new CircumferenceSegment(new Circumference(dilationCenter, circumferenceSegment.getCircumference().getRadius() - circumference.getRadius()), circumferenceSegment.getAngleInterval()));
				}
				else if ( circumferenceSegment.getCircumference().getRadius() < circumference.getRadius() ) {
					shapes.add(new CircumferenceSegment(new Circumference(dilationCenter, circumference.getRadius() - circumferenceSegment.getCircumference().getRadius()), circumferenceSegment.getAngleInterval().add((float) Math.PI)));
				}
				
				shapes.add(new CircumferenceSegment(new Circumference(dilationCenter, circumferenceSegment.getCircumference().getRadius() + circumference.getRadius()), circumferenceSegment.getAngleInterval()));
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(shapes);
			}
			
		},
		new Bimethod.Case<Void, ClosedSurface<?>, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>( (Class<ClosedSurface<?>>) (Class<?>) ClosedSurface.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, ClosedSurface<?> closedSurface,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				return closedSurface.getPerimeter().perimetralDilate(otherShape);
			}
			
		},
		new Bimethod.Case<Void, FiniteProportionalHolomorphicShapeGroup<?>, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>((Class<FiniteProportionalHolomorphicShapeGroup<?>>) (Class<?>) FiniteProportionalHolomorphicShapeGroup.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, FiniteProportionalHolomorphicShapeGroup<?> proportionalHolomorphicShapeGroup,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				final List<FiniteProportionalHolomorphicShape<?>> shapes = new ArrayList<FiniteProportionalHolomorphicShape<?>>(proportionalHolomorphicShapeGroup.getShapes().size());
				
				for ( FiniteProportionalHolomorphicShape<?> eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
					shapes.add( eachShape.perimetralDilate(otherShape) );
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(shapes);
			}
			
		},
		new Bimethod.Case<Void, FiniteAffineHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>>((Class<FiniteAffineHolomorphicShape<?>>) (Class<?>) FiniteAffineHolomorphicShape.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public FiniteProportionalHolomorphicShape<?> process(Void parameters, FiniteAffineHolomorphicShape<?> finiteAffineHolomorphicShape, FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
				return finiteAffineHolomorphicShape.getBackingShape().perimetralDilate(proportionalHolomorphicShape);
			}
			
		}
	);
	
	@SuppressWarnings("unchecked")
	private static final SymmetricBimethod<Void, FiniteProportionalHolomorphicShape<?>, Boolean> intersectionTestBimethod = SymmetricBimethod.make( (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class,
		new Bimethod.Case<Void, Point, FiniteProportionalHolomorphicShape<?>, Boolean>(Point.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public Boolean process(Void parameters, Point point,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				return otherShape.contains(point.getPosition());
			}
			
		},
		new Bimethod.Case<Void, LineSegment, Curve<?>, Boolean>(LineSegment.class, (Class<Curve<?>>) (Class<?>) Curve.class) {

			@Override
			public Boolean process(Void parameters, LineSegment lineSegment, Curve<?> curve) {
				boolean hasIntersection = false;
				
				final Iterator<Float> intersectionIterator = curve.getRectIntersection(lineSegment.getRect()).iterator();
				
				while ( intersectionIterator.hasNext() && (!hasIntersection) ) {
					hasIntersection = new FloatClosedInterval(0.0f, 1.0f).contains(intersectionIterator.next());
				}
				
				return hasIntersection;
			}
			
		},
		new Bimethod.Case<Void, Circumference, Circumference, Boolean>(Circumference.class, Circumference.class) {

			@Override
			public Boolean process(Void parameters, Circumference circumference1, Circumference circumference2) {
				final Circumference minCircumference, maxCircumference;
				
				if ( circumference1.getRadius() > circumference2.getRadius() ) {
					maxCircumference = circumference1;
					minCircumference = circumference2;
				}
				else {
					minCircumference = circumference1;
					maxCircumference = circumference2;
				}
				
				return new FloatClosedInterval(ExtraMath.square(maxCircumference.getRadius()-minCircumference.getRadius()), ExtraMath.square(maxCircumference.getRadius()+minCircumference.getRadius())).contains(minCircumference.getCenter().sub(maxCircumference.getCenter()).lengthSquared());
			}
			
		},
		new Bimethod.Case<Void, CircumferenceSegment, Circumference, Boolean>(CircumferenceSegment.class, Circumference.class) {

			@Override
			public Boolean process(Void parameters, CircumferenceSegment circumferenceSegment, Circumference circumference) {
				final Iterator<Vector2f> circumferenceAnglesIterator = circumference.getIntersections(circumferenceSegment.getCircumference()).iterator();
				boolean hasIntersection = false;
				
				while ( circumferenceAnglesIterator.hasNext() && (!hasIntersection) ) {
					hasIntersection = Geometry.containsAngle(circumferenceSegment.getAngleInterval(), circumferenceAnglesIterator.next().sub(circumferenceSegment.getCircumference().getCenter()).getAngle());
				}
				
				return hasIntersection;
			}
			
		},
		new Bimethod.Case<Void, CircumferenceSegment, CircumferenceSegment, Boolean>(CircumferenceSegment.class, CircumferenceSegment.class) {

			@Override
			public Boolean process(Void parameters, CircumferenceSegment circumferenceSegment1, CircumferenceSegment circumferenceSegment2) {
				final Iterator<Vector2f> circumferenceAnglesIterator = circumferenceSegment1.getCircumference().getIntersections(circumferenceSegment2.getCircumference()).iterator();
				boolean hasIntersection = false;
				
				while ( circumferenceAnglesIterator.hasNext() && (!hasIntersection) ) {
					final Vector2f eachPoint = circumferenceAnglesIterator.next();
					hasIntersection = 
							Geometry.containsAngle(circumferenceSegment1.getAngleInterval(), eachPoint.sub(circumferenceSegment1.getCircumference().getCenter()).getAngle()) &&
							Geometry.containsAngle(circumferenceSegment2.getAngleInterval(), eachPoint.sub(circumferenceSegment2.getCircumference().getCenter()).getAngle())
					;
				}
				
				return hasIntersection;
			}
			
		},
		new Bimethod.Case<Void, FiniteAffineHolomorphicShape<?>, FiniteProportionalHolomorphicShape<?>, Boolean>((Class<FiniteAffineHolomorphicShape<?>>) (Class<?>) FiniteAffineHolomorphicShape.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public Boolean process(Void parameters, FiniteAffineHolomorphicShape<?> finiteAffineHolomorphicShape, FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
				return finiteAffineHolomorphicShape.getBackingShape().hasIntersection(proportionalHolomorphicShape);
			}
			
		},
		new Bimethod.Case<Void, ClosedSurface<?>, FiniteProportionalHolomorphicShape<?>, Boolean>((Class<ClosedSurface<?>>) (Class<?>) ClosedSurface.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public Boolean process(Void parameters, ClosedSurface<?> closedSurface, FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
				/**
				 * Si hay intersección con el perímetro, entonces hay intersección con la superficie cerrada.
				 * Si no hay intersección con el perímetro entonces quedan dos posibilidades,
				 * la figura queda contenida enteramente en la superficie cerrada, o
				 * está completamente fuera.
				 * En el primer caso se puede asegurar que cualquier punto pertenece a la superficie cerrada.
				 * Si ningún punto extremo está contenido en la superficie cerrada entonces no hay
				 * intersección, ya que tampoco hay intersección con el perímetro.
				 * O sea está fuera.
				 */
				
				return closedSurface.contains(proportionalHolomorphicShape.getInnerPoint()) || closedSurface.getPerimeter().hasIntersection(proportionalHolomorphicShape);
			}
			
		},
		new Bimethod.Case<Void, FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>, FiniteProportionalHolomorphicShape<?>, Boolean>((Class<FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>>) (Class<?>) FiniteProportionalHolomorphicShapeGroup.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public Boolean process(Void parameters,
					FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>> proportionalHolomorphicShapeGroup,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				Iterator<FiniteProportionalHolomorphicShape<?>> shapeIterator = proportionalHolomorphicShapeGroup.getShapes().iterator();
				boolean hasIntersection = false;
				
				while ( shapeIterator.hasNext() && (!hasIntersection) ) {
					hasIntersection = shapeIterator.next().hasIntersection(otherShape);
				}
				
				return hasIntersection;
			}
			
		}
	);
	
	public final static class NearestPointBetweenShapes {
		private final Vector2f point;
		private final float distanceBetweenShapes;
		
		public NearestPointBetweenShapes(Vector2f point, float distanceBetweenShapes) {
			if ( point != null ) {
				if ( distanceBetweenShapes >= 0 ) {
					this.point = point;
					this.distanceBetweenShapes = distanceBetweenShapes;
				}
				else {
					throw new IllegalArgumentException("Illegal distance");
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el punto
		 */
		public Vector2f getPoint() {
			return this.point;
		}
		
		/**
		 * @post Devuelve la distancia
		 */
		public float getDistanceBetweenShapes() {
			return this.distanceBetweenShapes;
		}
	}
	
	private static NearestPointBetweenShapes getNearestPointBetweenShapesFromArray(NearestPointBetweenShapes... nearestPointBetweenShapes) {
		NearestPointBetweenShapes result = null;
		for ( NearestPointBetweenShapes eachPoint : nearestPointBetweenShapes ) {
			if ( eachPoint != null ) {
				if ( ( result == null ) || ( eachPoint.distanceBetweenShapes < result.distanceBetweenShapes ) ) {
					result = eachPoint;
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static final SymmetricBimethod<Void, FiniteProportionalHolomorphicShape<?>, NearestPointBetweenShapes> nearestPointBetweenShapePerimetersBimethod = SymmetricBimethod.make( (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class,
		new Bimethod.Case<Void, Point, FiniteProportionalHolomorphicShape<?>, NearestPointBetweenShapes>(Point.class, (Class< FiniteProportionalHolomorphicShape<?> >) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, Point point, FiniteProportionalHolomorphicShape<?> otherShape) {
				final Vector2f relativePoint = otherShape.translate(point.getPosition().opposite()).accept(new Visitor<Vector2f, RuntimeException>() {

					@Override
					public Vector2f visit(Point point) throws RuntimeException {
						return point.getPosition();
					}
					
					/**
					 * @post Devuelve el punto más cercano
					 */
					private Vector2f nearestPoint(Vector2f... points) {
						Vector2f resultPoint = null;
						float resultPointDistance = Float.POSITIVE_INFINITY;
						
						for ( Vector2f eachPoint : points ) {
							final float eachPointDistance = eachPoint.length();
							
							if ( eachPointDistance < resultPointDistance ) {
								resultPoint = eachPoint;
								resultPointDistance = eachPointDistance;
							}
						}
						
						return resultPoint;
					}
					
					@Override
					public Vector2f visit(LineSegment lineSegment) throws RuntimeException {
						final Line perpendicularLine = new Line(Vector2f.ZERO, lineSegment.getVector12().rotate90AnticlockWise());
						final Float intersection = lineSegment.getRectIntersectionPoint(perpendicularLine);
						final Vector2f[] points;
						
						if ( intersection != null ) {
							points = new Vector2f[3];
							points[2] = perpendicularLine.getPointByProportionalScalar(intersection);
						}
						else {
							points = new Vector2f[2];
						}
						
						points[0] = lineSegment.getPoint1();
						points[1] = lineSegment.getPoint2();
						
						return nearestPoint(points);
					}

					@Override
					public Vector2f visit(Circumference circumference) throws RuntimeException {
						final float distanceToCenter = circumference.getCenter().length();
						final Vector2f result;
						
						if ( distanceToCenter != 0.0f ) {
							result = circumference.getCenter().scale((distanceToCenter-circumference.getRadius())/distanceToCenter);
						}
						else {
							result = new Vector2f(circumference.getRadius(), 0.0f);
						}
						
						return result;
					}

					@Override
					public Vector2f visit(CircumferenceSegment circumferenceSegment) throws RuntimeException {
						final float distanceToCenter = circumferenceSegment.getCircumference().getCenter().length();
						final float centerAngle = circumferenceSegment.getCircumference().getCenter().getAngle();

						final Vector2f result;
						
						if ( distanceToCenter != 0.0f ) {
							final float circumferenceNearestPointAngle = centerAngle + (float) Math.PI;
							
							if ( Geometry.containsAngle(circumferenceSegment.getAngleInterval(), circumferenceNearestPointAngle) ) {
								result = circumferenceSegment.getCircumference().getPointWithAngle(circumferenceNearestPointAngle);
							}
							else {
								result = nearestPoint(circumferenceSegment.getLimitPoint1(), circumferenceSegment.getLimitPoint2());
							}
						}
						else {
							result = circumferenceSegment.getLimitPoint1();
						}
						
						return result;
					}

					@Override
					public Vector2f visit(Circle circle) throws RuntimeException {
						return circle.getPerimeter().accept(this);
					}

					@Override
					public Vector2f visit(SolidCapsule capsule) throws RuntimeException {
						return capsule.getPerimeter().accept(this);
					}

					@Override
					public Vector2f visit(ConvexPolygon convexPolygon) throws RuntimeException {
						return convexPolygon.getPerimeter().accept(this);
					}

					@Override
					public <S extends FiniteProportionalHolomorphicShape<S>> Vector2f visit(
							FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
						return finiteAffineHolomorphicShape.accept(this);
					}

					@Override
					public <S extends FiniteProportionalHolomorphicShape<?>> Vector2f visit(
							FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup)
							throws RuntimeException {
						Vector2f resultPoint = null;
						float resultPointDistance = Float.POSITIVE_INFINITY;
						
						for ( S eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
							final Vector2f eachNearestPoint = eachShape.accept(this);
							final float eachPointDistance = eachNearestPoint.length();
							
							if ( eachPointDistance < resultPointDistance ) {
								resultPoint = eachNearestPoint;
								resultPointDistance = eachPointDistance;
							}
						}
						
						return resultPoint;
					}
					
				});
				return new NearestPointBetweenShapes(relativePoint.scale(0.5f).add(point.getPosition()), relativePoint.length());
			}
			
		},
		new Bimethod.Case<Void, LineSegment, LineSegment, NearestPointBetweenShapes>(LineSegment.class, LineSegment.class) {
			@Override
			public NearestPointBetweenShapes process(Void parameters, LineSegment lineSegment1, LineSegment lineSegment2) {
				return getNearestPointBetweenShapesFromArray(
					lineSegment1.nearestPointBetweenShapePerimeters(new Point(lineSegment2.getPoint1())),
					lineSegment1.nearestPointBetweenShapePerimeters(new Point(lineSegment2.getPoint2())),
					lineSegment2.nearestPointBetweenShapePerimeters(new Point(lineSegment1.getPoint1())),
					lineSegment2.nearestPointBetweenShapePerimeters(new Point(lineSegment1.getPoint2()))
				);
			}
			
		},
		new Bimethod.Case<Void, LineSegment, Circumference, NearestPointBetweenShapes>(LineSegment.class, Circumference.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, LineSegment lineSegment, Circumference circumference) {
				final Line perpendicularLine = new Line(circumference.getCenter(), lineSegment.getVector12().rotate90AnticlockWise());
				final Float intersection = lineSegment.getRectIntersectionPoint(perpendicularLine);
				final NearestPointBetweenShapes result;
				
				if ( intersection != null ) {
					final Vector2f perpendicularIntersection = perpendicularLine.getPointByProportionalScalar(intersection);
					final Vector2f centerPerpendicularIntersection = perpendicularIntersection.sub(circumference.getCenter());
					final float centerPerpendicularIntersection_length = centerPerpendicularIntersection.length();
					
					result = new NearestPointBetweenShapes( perpendicularIntersection.add( centerPerpendicularIntersection.scale(circumference.getRadius() / centerPerpendicularIntersection_length).add(circumference.getCenter()) ).scale(0.5f), Math.abs( circumference.getRadius() - centerPerpendicularIntersection_length) );
				}
				else {
					result = circumference.nearestPointBetweenShapePerimeters(new FiniteProportionalHolomorphicShapeGroup<Point>(new Point(lineSegment.getPoint1()), new Point(lineSegment.getPoint2())));
				}
				
				return result;
			}
			
		},
		new Bimethod.Case<Void, LineSegment, CircumferenceSegment, NearestPointBetweenShapes>(LineSegment.class, CircumferenceSegment.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, LineSegment lineSegment,
					CircumferenceSegment circumferenceSegment) {
				final Line perpendicularLine = new Line(circumferenceSegment.getCircumference().getCenter(), lineSegment.getVector12().rotate90AnticlockWise());
				final Float intersection = lineSegment.getRectIntersectionPoint(perpendicularLine);
				NearestPointBetweenShapes result = null;
				
				if ( intersection != null ) {
					final Vector2f perpendicularIntersection = perpendicularLine.getPointByProportionalScalar(intersection);
					final Vector2f centerPerpendicularIntersection = perpendicularIntersection.sub(circumferenceSegment.getCircumference().getCenter());
					
					if ( Geometry.containsAngle(circumferenceSegment.getAngleInterval(), centerPerpendicularIntersection.getAngle() ) ) {
						final float centerPerpendicularIntersection_length = centerPerpendicularIntersection.length();
						
						result = new NearestPointBetweenShapes( perpendicularIntersection.add( centerPerpendicularIntersection.scale(circumferenceSegment.getCircumference().getRadius() / centerPerpendicularIntersection_length).add(circumferenceSegment.getCircumference().getCenter()) ).scale(0.5f), Math.abs( circumferenceSegment.getCircumference().getRadius() - centerPerpendicularIntersection_length ) );
					}
				}
				
				if ( result == null ) {
					result = getNearestPointBetweenShapesFromArray(
						lineSegment.nearestPointBetweenShapePerimeters(new FiniteProportionalHolomorphicShapeGroup<Point>(new Point(circumferenceSegment.getLimitPoint1()), new Point(circumferenceSegment.getLimitPoint2()))),
						circumferenceSegment.nearestPointBetweenShapePerimeters(new FiniteProportionalHolomorphicShapeGroup<Point>(new Point(lineSegment.getPoint1()), new Point(lineSegment.getPoint2())))
					);
				}
				
				return result;
			}
			
		},
		new Bimethod.Case<Void, Circumference, Circumference, NearestPointBetweenShapes>(Circumference.class, Circumference.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, Circumference circumference1, Circumference circumference2) {
				if ( circumference2.getRadius() < circumference1.getRadius() ) {
					final Circumference temp = circumference2;
					circumference2 = circumference1;
					circumference1 = temp;
				}
				
				final Vector2f center21 = circumference2.getCenter().sub(circumference1.getCenter());
				final float center21distance = center21.length();
				final float distanceToPoint1;
				final float distanceBetweenPerimeters;
				
				if ( center21distance < circumference2.getRadius() ) {
					distanceToPoint1 = ( -circumference1.getRadius() - ( circumference2.getRadius() - center21distance ) ) / 2.0f;
					distanceBetweenPerimeters = circumference2.getRadius() - center21distance - circumference1.getRadius();
				}
				else {
					distanceToPoint1 = ( circumference1.getRadius() + ( center21distance - circumference2.getRadius() ) ) / 2.0f;
					distanceBetweenPerimeters = center21distance - circumference1.getRadius() - circumference2.getRadius();
				}
				
				return new NearestPointBetweenShapes(circumference1.getCenter().add(center21.scale(distanceToPoint1 / center21distance)), Math.max(0.0f, distanceBetweenPerimeters));
			}
			
		},
		new Bimethod.Case<Void, CircumferenceSegment, Circumference, NearestPointBetweenShapes>(CircumferenceSegment.class, Circumference.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, CircumferenceSegment circumferenceSegment, Circumference circumference) {
				final float tangentNearestAngle;
				final Vector2f centerCircumferenceCircumferenceSegment = circumferenceSegment.getCircumference().getCenter().sub(circumference.getCenter());
				final float centerCircumferenceCircumferenceSegmentLength = centerCircumferenceCircumferenceSegment.length();
				final float centerCircumferenceCircumferenceSegmentAngle = centerCircumferenceCircumferenceSegment.getAngle();
				
				if ( centerCircumferenceCircumferenceSegmentLength < circumference.getRadius() ) {
					tangentNearestAngle = centerCircumferenceCircumferenceSegmentAngle;
				}
				else {
					tangentNearestAngle = centerCircumferenceCircumferenceSegmentAngle + (float) Math.PI;
				}
				
				final Vector2f circumferenceSegmentNearestPoint;
				
				if ( Geometry.containsAngle(circumferenceSegment.getAngleInterval(), tangentNearestAngle) ) {
					circumferenceSegmentNearestPoint = circumferenceSegment.getCircumference().getPointWithAngle(tangentNearestAngle);
				}
				else {
					final float distance1 = Math.abs(circumferenceSegment.getLimitPoint1().sub(circumference.getCenter()).length() - circumference.getRadius());
					final float distance2 = Math.abs(circumferenceSegment.getLimitPoint2().sub(circumference.getCenter()).length() - circumference.getRadius());
					
					if ( distance1 < distance2 ) {
						circumferenceSegmentNearestPoint = circumferenceSegment.getLimitPoint1();
					}
					else {
						circumferenceSegmentNearestPoint = circumferenceSegment.getLimitPoint2();
					}
				}
				
				final Vector2f relativeCircumferenceSegmentNearestPoint = circumferenceSegmentNearestPoint.sub(circumference.getCenter());
				final float relativeCircumferenceSegmentNearestPointLength = relativeCircumferenceSegmentNearestPoint.length();
				
				return new NearestPointBetweenShapes(circumference.getCenter().add(relativeCircumferenceSegmentNearestPoint.scale((circumference.getRadius() + relativeCircumferenceSegmentNearestPointLength) / 2.0f / relativeCircumferenceSegmentNearestPointLength)), Math.abs( relativeCircumferenceSegmentNearestPointLength - circumference.getRadius() ) );
			}
			
		},
		new Bimethod.Case<Void, CircumferenceSegment, CircumferenceSegment, NearestPointBetweenShapes>(CircumferenceSegment.class, CircumferenceSegment.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, CircumferenceSegment circumferenceSegment1,
					CircumferenceSegment circumferenceSegment2) {
				if ( circumferenceSegment2.getCircumference().getRadius() < circumferenceSegment1.getCircumference().getRadius() ) {
					final CircumferenceSegment temp = circumferenceSegment2;
					circumferenceSegment2 = circumferenceSegment1;
					circumferenceSegment1 = temp;
				}
				
				final Vector2f center21 = circumferenceSegment2.getCircumference().getCenter().sub(circumferenceSegment1.getCircumference().getCenter());
				final float center21distance = center21.length();
				final float center21angle = center21.getAngle();
				NearestPointBetweenShapes result;
				
				if ( ( center21distance > circumferenceSegment1.getCircumference().getRadius() + circumferenceSegment2.getCircumference().getRadius() ) && Geometry.containsAngle( circumferenceSegment1.getAngleInterval(), center21angle ) && Geometry.containsAngle( circumferenceSegment2.getAngleInterval(), center21angle + (float) Math.PI ) ) {
					final float distanceBetweenShapes = center21distance - circumferenceSegment1.getCircumference().getRadius() - circumferenceSegment2.getCircumference().getRadius();
					
					result = new NearestPointBetweenShapes(
						circumferenceSegment1.getCircumference().getCenter().add( center21.scale( ( circumferenceSegment1.getCircumference().getRadius() + distanceBetweenShapes / 2.0f ) / center21distance) ),
						distanceBetweenShapes
					);
				}
				else {
					NearestPointBetweenShapes[] nearestPointsBetweenShapes = new NearestPointBetweenShapes[4];
					
					if ( ( circumferenceSegment2.getCircumference().getRadius() >= circumferenceSegment1.getCircumference().getRadius() - center21distance ) && Geometry.containsAngle( circumferenceSegment1.getAngleInterval(), center21angle ) && Geometry.containsAngle( circumferenceSegment2.getAngleInterval(), center21angle ) ) {
						final float distanceBetweenShapes = circumferenceSegment2.getCircumference().getRadius() - circumferenceSegment1.getCircumference().getRadius() + center21distance;
						
						nearestPointsBetweenShapes[0] = new NearestPointBetweenShapes(
							circumferenceSegment1.getCircumference().getCenter().add( center21.scale( ( circumferenceSegment1.getCircumference().getRadius() + distanceBetweenShapes / 2.0f ) / center21distance) ),
							distanceBetweenShapes
						);
					}
					else {
						nearestPointsBetweenShapes[0] = null;
					}
					
					if ( ( circumferenceSegment2.getCircumference().getRadius() >= circumferenceSegment1.getCircumference().getRadius() + center21distance ) && Geometry.containsAngle( circumferenceSegment1.getAngleInterval(), center21angle + (float) Math.PI ) && Geometry.containsAngle( circumferenceSegment2.getAngleInterval(), center21angle + (float) Math.PI ) ) {
						final float distanceBetweenShapes = circumferenceSegment2.getCircumference().getRadius() - circumferenceSegment1.getCircumference().getRadius() - center21distance;
						
						nearestPointsBetweenShapes[1] = new NearestPointBetweenShapes(
							circumferenceSegment1.getCircumference().getCenter().add( center21.scale( -( circumferenceSegment1.getCircumference().getRadius() + distanceBetweenShapes / 2.0f ) / center21distance) ),
							distanceBetweenShapes
						);
					}
					else {
						nearestPointsBetweenShapes[1] = null;
					}
					
					nearestPointsBetweenShapes[2] = circumferenceSegment2.nearestPointBetweenShapePerimeters( new FiniteProportionalHolomorphicShapeGroup<Point>(new Point(circumferenceSegment1.getLimitPoint1()), new Point(circumferenceSegment1.getLimitPoint2())) );
					nearestPointsBetweenShapes[3] = circumferenceSegment1.nearestPointBetweenShapePerimeters( new FiniteProportionalHolomorphicShapeGroup<Point>(new Point(circumferenceSegment2.getLimitPoint1()), new Point(circumferenceSegment2.getLimitPoint2())) );
					
					result = getNearestPointBetweenShapesFromArray(nearestPointsBetweenShapes);
				}
				
				return result;
			}
			
		},
		new Bimethod.Case<Void, ClosedSurface<?>, FiniteProportionalHolomorphicShape<?>, NearestPointBetweenShapes>((Class<ClosedSurface<?>>) (Class<?>) ClosedSurface.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters, ClosedSurface<?> closedSurface, FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
				return closedSurface.getPerimeter().nearestPointBetweenShapePerimeters(proportionalHolomorphicShape);
			}
		},
		new Bimethod.Case<Void, FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>, FiniteProportionalHolomorphicShape<?>, NearestPointBetweenShapes>((Class<FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>>) (Class<?>) FiniteProportionalHolomorphicShapeGroup.class, (Class<FiniteProportionalHolomorphicShape<?>>) (Class<?>) FiniteProportionalHolomorphicShape.class) {

			@Override
			public NearestPointBetweenShapes process(Void parameters,
					FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>> proportionalHolomorphicShapeGroup,
					FiniteProportionalHolomorphicShape<?> otherShape) {
				NearestPointBetweenShapes result = null;
				
				for ( FiniteProportionalHolomorphicShape<?> eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
					final NearestPointBetweenShapes eachResult = eachShape.nearestPointBetweenShapePerimeters(otherShape);
					
					if ( ( result == null ) || ( eachResult.distanceBetweenShapes < result.distanceBetweenShapes ) ) {
						result = eachResult;
					}
				}
				
				return result;
			}
		}
	);
	
	/**
	 * @post Crea la figura
	 */
	FiniteProportionalHolomorphicShape() {
		
	}
	
	/**
	 * @pre El desplazamiento no puede ser nulo
	 * @post Calcula la translación especificada
	 */
	public abstract S translate(Vector2f displacement);
	
	/**
	 * @post Devuelve el resultado de la transformación con la matriz especificada
	 */
	public abstract S transform(ProportionalMatrix3f matrix);
	
	/**
	 * @pre La figura no puede ser nula
	 * @post Devuelve el resultado perimetral de la dilatación con la figura especificada
	 */
	public final FiniteProportionalHolomorphicShape<?> perimetralDilate(FiniteProportionalHolomorphicShape<?> other) {
		if ( other != null ) {
			return perimetralDilationBimethod.process(null, this, other);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una figura de envoltura afín
	 */
	public abstract FiniteAffineHolomorphicShape<? extends FiniteProportionalHolomorphicShape<?>> getBoundingAffineHolomorphicShape();
	
	/**
	 * @pre La figura no puede ser nula
	 * @post Devuelve si hay intersección con la figura especificada
	 */
	public final boolean hasIntersection(FiniteProportionalHolomorphicShape<?> other) {
		if ( other != null ) {
			return intersectionTestBimethod.process(null, this, other);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve si contiene el punto especificado
	 */
	@Override
	public abstract boolean contains(Vector2f point);
	
	/**
	 * @post Devuelve el punto más interior, si el conjunto no es acotado o es una curva cerrada
	 * 		 devuelve un punto arbitrario
	 */
	protected abstract Vector2f getInnerPoint();
	
	/**
	 * @post Calcula el bounding box
	 */
	public abstract BoundingBox boundingBox();
	
	/**
	 * @post Devuelve la distancia máxima al origen de coordenadas
	 */
	public abstract float maxDistanceToOrigin();
	
	/**
	 * @post Devuelve la distancia mínima al origen de coordenadas
	 */
	public abstract float minDistanceToOrigin();
	
	/**
	 * @pre La otra figura no puede ser nula, y no tiene que haber
	 * 		solapamiento
	 * @post Calcula la distancia perimetral con la figura especificada
	 */
	public final float perimetralDistance(FiniteProportionalHolomorphicShape<?> other) {
		if ( other != null ) {
			return this.perimetralDilate(other.opposite()).minDistanceToOrigin();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public NearestPointBetweenShapes nearestPointBetweenShapePerimeters(FiniteProportionalHolomorphicShape<?> other) {
		if ( other != null ) {
			return nearestPointBetweenShapePerimetersBimethod.process(null, this, other);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El visitor no puede ser nulo
	 * @post Procesa la figura con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
