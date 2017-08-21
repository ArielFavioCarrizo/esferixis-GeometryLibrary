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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.plane.exception.ProportionalHolomorphicShapeParseException;
import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.CircumferenceSegment;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShapeGroup;
import com.esferixis.geometry.plane.finite.LineSegment;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.geometry.plane.finite.SolidCapsule;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.binaryclassifier.BinaryClassifier;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.ParametrizedFunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public abstract class Shape<S extends Shape<S>> {
	public static interface Visitor<V, T extends Throwable> {
		/**
		 * @post Visita una línea
		 */
		public V visit(Line line) throws T;
		
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
	
	public static final class NearestNormal {
		private final Vector2f value;
		private final float distance;
		
		/**
		 * @pre El valor no puede ser nulo ni el vector cero, y la distancia tiene
		 * 		que ser positiva
		 * @post Crea la normal con la distancia especificada
		 */
		public NearestNormal(Vector2f value, float distance) {
			if ( value != null ) {
				if ( !value.equals(Vector2f.ZERO) ) {
					if ( distance >= 0.0f ) {
						this.value = value;
						this.distance = distance;
					}
					else {
						throw new IllegalArgumentException("Invalid normal distance");
					}
				}
				else {
					throw new IllegalArgumentException("Invalid normal value");
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el valor
		 */
		public Vector2f getValue() {
			return this.value;
		}
		
		/**
		 * @post Devuelve la distancia
		 */
		public float getDistance() {
			return this.distance;
		}
	}
	
	private static ExpressionParser<Shape<?>> parser = new ExpressionParser<Shape<?>>(
			new ParametrizedFunctionParser<Circle>("Circle") {

				@Override
				public Circle parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 1);
					return new Circle( Shape.parse( parameters.get(0), Circumference.class ) );
				}
				
			},
			new ParametrizedFunctionParser<Circumference>("Circumference") {

				@Override
				public Circumference parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 2);
					return new Circumference( Vector2f.parse( parameters.get(0) ), Float.parseFloat( parameters.get(1) ) );
				}
				
			},
			new ParametrizedFunctionParser<CircumferenceSegment>("CircumferenceSegment") {

				@Override
				public CircumferenceSegment parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 2);
					return new CircumferenceSegment( Shape.parse( parameters.get(0), Circumference.class ), FloatClosedInterval.parse( parameters.get(1) ) );
				}
				
			},
			new ParametrizedFunctionParser<ConvexPolygon>("ConvexPolygon") {

				@Override
				public ConvexPolygon parse(List<String> parameters) throws ParseException {
					List<Vector2f> vertices = new ArrayList<Vector2f>(parameters.size());
					for ( String eachParameter : parameters ) {
						vertices.add(Vector2f.parse(eachParameter));
					}
					return new ConvexPolygon(vertices);
				}
				
			},
			new ParametrizedFunctionParser<Line>("Line") {

				@Override
				public Line parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 2);
					return new Line( Vector2f.parse(parameters.get(0)), Vector2f.parse(parameters.get(1)) );
				}
				
			},
			new ParametrizedFunctionParser<LineSegment>("LineSegment") {

				@Override
				public LineSegment parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 2);
					return new LineSegment( Vector2f.parse(parameters.get(0)), Vector2f.parse(parameters.get(1)) );
				}
				
			},
			new ParametrizedFunctionParser<SolidCapsule>("SolidCapsule") {

				@Override
				public SolidCapsule parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 2);
					return new SolidCapsule( Shape.parse(parameters.get(0), LineSegment.class), Float.parseFloat(parameters.get(1)) );
				}
				
			},
			new ParametrizedFunctionParser<Point>("Point") {

				@Override
				public Point parse(List<String> parameters) throws ParseException {
					ExpressionParser.checkParametersQuantity(parameters, 1);
					return new Point( Vector2f.parse(parameters.get(0)) );
				}
				
			},
			new ParametrizedFunctionParser<FiniteProportionalHolomorphicShapeGroup<?>>("ProportionalHolomorphicShapeGroup") {

				@Override
				public FiniteProportionalHolomorphicShapeGroup<?> parse(List<String> parameters) throws ParseException {
					List<FiniteProportionalHolomorphicShape<?>> shapes = new ArrayList<FiniteProportionalHolomorphicShape<?>>(parameters.size());
					for ( String eachParameter : parameters ) {
						shapes.add( Shape.parse(eachParameter, FiniteProportionalHolomorphicShape.class) );
					}
					return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(shapes);
				}
				
			}
	);
	
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
	 * @post Obtiene una intersección de rayo a partir de las intersecciones
	 * 		 especificadas.
	 *       Si no encuentra devuelve null
	 */
	protected static Float getRayIntersection(Iterable<Float> intersections) {
		return getRayIntersection(intersections, null);
	}
	
	/**
	 * @post Obtiene una intersección de rayo a partir de las intersecciones
	 * 		 especificadas, con el filtro especificado.
	 * 		 Puede indicarse que no se desea un filtro, poniéndolo como null
	 *       Si no encuentra devuelve null.
	 */
	protected static Float getRayIntersection(Iterable<Float> intersections, BinaryClassifier<Float> filter) {
		Float minValue = null;
		
		for ( Float eachValue : intersections ) {
			if ( eachValue != null ) {
				if ( eachValue >= 0.0f ) {
					if ( ( filter == null ) || filter.evaluate(eachValue) ) {
						if ( ( minValue == null ) || ( eachValue < minValue ) ) {
							minValue = eachValue;
						}
					}
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		return minValue;
	}
	
	/**
	 * @post Devuelve si contiene el punto especificado
	 */
	public abstract boolean contains(Vector2f point);
	
	/**
	 * @post Devuelve todas las intersecciones con la recta especificada
	 */
	public abstract List<Float> getRectIntersection(Line rect);
	
	/**
	 * @post Devuelve la intersección más cercana contando desde el punto de referencia
	 * 		 de la recta, como si fuese un rayo
	 * 
	 * 		 Si no hay intersección devuelve null.
	 */
	public Float getRayIntersection(Line rect) {
		return getRayIntersection(this.getRectIntersection(rect));
	}
	
	/**
	 * @post Devuelve el opuesto aritmético
	 */
	public abstract S opposite();
	
	/**
	 * @post Devuelve la normal más cercana al origen, si no existe devuelve null
	 */
	public abstract NearestNormal nearestNormalToOrigin();
	
	private static List<String> separateParameters(String parametersString) throws ProportionalHolomorphicShapeParseException {
		int bracesLevel = 0;
		ArrayList<String> parameters = new ArrayList<String>();
		
		int parameterStartIndex = 0;
		
		for ( int i = 0 ; i <= parametersString.length() ; i++ ) {
			final boolean end = ( i == parametersString.length() );
	
			if ( !end ) {
				switch ( parametersString.charAt(i) ) {
				case '(':					
				case '[':
					bracesLevel++;
					break;
				case ')':
				case ']':
					bracesLevel--;
					
					if ( bracesLevel < 0 ) {
						throw new ProportionalHolomorphicShapeParseException("Missing brace '('");
					}
					
					break;
				}
			}
			
			if ( end || ( parametersString.charAt(i) == ',' ) ) {
				if ( bracesLevel == 0 ) {
					parameters.add( parametersString.substring(parameterStartIndex, i).trim() );
					parameterStartIndex = i + 1;
				}
			}
		}
		
		if ( bracesLevel > 0 ) {
			throw new ProportionalHolomorphicShapeParseException("Missing brace ')'");
		}
		
		if ( ( parameters.size() == 1 ) && ( parameters.get(0).isEmpty() ) ) {
			parameters.clear();
		}
		
		parameters.trimToSize();
		
		return Collections.unmodifiableList(parameters);
	}
	
	/**
	 * @post Convierte una cadena de carácteres en una figura, esperando
	 * 		 la clase de figura especificada
	 */
	public static <S extends Shape<?>> S parse(String entityString, Class<S> filterShapeClass) throws ProportionalHolomorphicShapeParseException {
		return parser.parse(entityString, filterShapeClass);
	}
	
	/**
	 * @pre El visitor no puede ser nulo
	 * @post Procesa la figura con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
