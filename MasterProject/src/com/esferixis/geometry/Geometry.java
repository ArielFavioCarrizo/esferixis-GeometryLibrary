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
package com.esferixis.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public class Geometry {
	private Geometry() {}
	
	private static final float doublePI = ExtraMath.doublePI;
	
	/**
	 * @post Halla la base del ángulo especificado
	 */
	public static float angleBase(float angle) {
		return (float) Math.floor(angle / doublePI) * doublePI;
	}
	
	/**
	 * @pre El intervalo no puede ser nulo
	 * @post Halla la base del intervalo angular especificado
	 */
	public static float angleBase(FloatClosedInterval angleInterval) {
		if ( angleInterval != null ) {
			return angleBase(angleInterval.getMin());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Normaliza el ángulo especificado en el intervalo de 0 a 2 * pi
	 */
	public static float normalise(float angle) {
		return angle - angleBase(angle);
	}
	
	/**
	 * @post Devuelve el ángulo mínimo tomando como referencia el ángulo mínimo
	 * 		 especificado.
	 * 		 Si sólo uno de ellos, devuelve ese ángulo.
	 * 		 Si ambos son nulos devuelve null
	 */
	public static Float minAngle(float min, Float angle1, Float angle2) {
		if ( ( angle1 != null ) && ( angle2 != null ) ) {
			min = normalise(min);
			angle1 = normalise(angle1);
			angle2 = normalise(angle2);
			
			if ( angle1 < min ) {
				angle1 += doublePI;
			}
			
			if ( angle2 < min ) {
				angle2 += doublePI;
			}
			
			return Math.min(angle1, angle2);
		}
		else if ( angle1 != null ) {
			return angle1;
		}
		else if ( angle2 != null ) {
			return angle2;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @pre El iterable no puede ser nulo
	 * @post Devuelve el ángulo mínimo con la base especificada del iterable especificado
	 */
	public Float minAngle(float minBase, Iterable<Float> angles) {
		if ( angles != null ) {
			Float minAngle = null;
			
			for ( Float eachAngle : angles ) {
				minAngle = minAngle(minBase, minAngle, eachAngle);
			}
			
			return minAngle;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El arrays de ángulos no puede ser nulo
	 * @post Devuelve el ángulo mínimo con la base especificada del iterable especificado
	 */
	public Float minAngle(float minBase, Float... angles) {
		if ( angles != null ) {
			return minAngle(minBase, Arrays.asList(angles));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el ángulo máximo tomando como referencia el ángulo mínimo
	 * 		 especificado.
	 * 		 Si sólo uno de ellos, devuelve ese ángulo.
	 * 		 Si ambos son nulos devuelve null
	 */
	public static Float maxAngle(float min, Float angle1, Float angle2) {
		if ( ( angle1 != null ) && ( angle2 != null ) ) {
			min = normalise(min);
			angle1 = normalise(angle1);
			angle2 = normalise(angle2);
			
			if ( angle1 < min ) {
				angle1 += doublePI;
			}
			
			if ( angle2 < min ) {
				angle2 += doublePI;
			}
			
			return Math.max(angle1, angle2);
		}
		else if ( angle1 != null ) {
			return angle1;
		}
		else if ( angle2 != null ) {
			return angle2;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @pre El iterable no puede ser nulo
	 * @post Devuelve el ángulo máximo con la base especificada del iterable especificado
	 */
	public Float maxAngle(float minBase, Iterable<Float> angles) {
		if ( angles != null ) {
			Float minAngle = null;
			
			for ( Float eachAngle : angles ) {
				minAngle = maxAngle(minBase, minAngle, eachAngle);
			}
			
			return minAngle;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El arrays de ángulos no puede ser nulo
	 * @post Devuelve el ángulo máximo con la base especificada del iterable especificado
	 */
	public Float maxAngle(float minBase, Float... angles) {
		if ( angles != null ) {
			return maxAngle(minBase, Arrays.asList(angles));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La diferencia entre los dos ángulos tiene que ser menor a 2*pi
	 * @post Crea un intervalo de ángulos con los ángulos especificados
	 */
	public static FloatClosedInterval makeAngleInterval(float startAngle, float endAngle) {
		startAngle = startAngle - angleBase(startAngle);
		endAngle = endAngle - angleBase(endAngle);
		
		if ( endAngle < startAngle ) {
			endAngle += doublePI;
		}
		
		return new FloatClosedInterval(startAngle, endAngle);
	}
	
	/**
	 * @post Calcula la intersección entre los dos intervalos de ángulos
	 * 		 especificados
	 * 
	 * 	 	 <ARREGLAR IMPLEMENTACIÓN>
	 */
	public static List<FloatClosedInterval> angleIntervalIntersection(FloatClosedInterval angleInterval1, FloatClosedInterval angleInterval2) {
		final List<FloatClosedInterval> result = new ArrayList<FloatClosedInterval>(2);
		
		if ( containsAngle(angleInterval1, angleInterval2.getMin()) ) {
			final float minBase = angleInterval2.getMin();
			
			final float maxMin = Geometry.minAngle(minBase, angleInterval1.getMin(), minBase);
			final float minMax = Geometry.minAngle(minBase, angleInterval1.getMax(), angleInterval2.getMax());
			
			if ( minMax >= maxMin ) {
				result.add(new FloatClosedInterval(maxMin, minMax));
			}
		}
		
		if ( containsAngle(angleInterval2, angleInterval1.getMin()) ) {
			final float minBase = angleInterval1.getMin();
			
			final float maxMin = Geometry.minAngle(minBase, angleInterval2.getMin(), minBase);
			final float minMax = Geometry.minAngle(minBase, angleInterval1.getMax(), angleInterval2.getMax());
			
			if ( minMax >= maxMin ) {
				result.add(new FloatClosedInterval(maxMin, minMax));
			}
		}
		
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * @pre El intervalo de ángulos no puede ser nulo
	 * @post Devuelve si el ángulo está contenido en el intervalo de ángulos
	 * 		 especificado
	 */
	public static boolean containsAngle(FloatClosedInterval angleInterval, float angle) {
		if ( angleInterval != null ) {
			angle = angle - angleBase(angle);
			
			angleInterval = angleInterval.sub( angleBase(angleInterval) );
			
			if ( angle < angleInterval.getMin() ) {
				angle += doublePI;
			}
			
			return angleInterval.contains(angle);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El intervalo de ángulos no puede ser nulo
	 * @post Devuelve si el ángulo está contenido en el intervalo de ángulos
	 * 		 especificado, excluyendo los extremos
	 */
	public static boolean containsAngleExcludingExtremes(FloatClosedInterval angleInterval, float angle) {
		if ( angleInterval != null ) {
			angle = angle - angleBase(angle);
			
			angleInterval = angleInterval.sub( angleBase(angleInterval) );
			
			if ( angle < angleInterval.getMin() ) {
				angle += doublePI;
			}
			
			return angleInterval.containsExcludingExtremes(angle);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve ángulos repartidos, conteniendo los límites, con una distancia entre ellos
	 * 		 no superior a 90°
	 * @param angleInterval
	 * @return
	 */
	public static float[] partialAngleIntervalSampling(FloatClosedInterval angleInterval) {
		final int tangentVertices;
		float[] angleSamples;
		
		tangentVertices = (int) Math.ceil( angleInterval.length() / (Math.PI / 2.0f) ) + 1;
		
		angleSamples = new float[tangentVertices];
		
		for ( int i = 0 ; i<tangentVertices; i++ ) {
			angleSamples[i] = angleInterval.linearInterpolation(i / (float) (tangentVertices-1));
		}
		
		return angleSamples;
	}
}
