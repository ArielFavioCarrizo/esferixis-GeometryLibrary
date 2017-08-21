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

import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public abstract class Curve<C extends FiniteProportionalHolomorphicShape<C>> extends FiniteProportionalHolomorphicShape<C> {
	private static final long serialVersionUID = 441194473268999843L;

	
	public interface Visitor<V, T extends Throwable> {
		public V visit(LineSegment lineSegment) throws T;
		public V visit(CircumferenceSegment circumferenceSegment) throws T;
		public V visit(Circumference circumference);
	}
	
	public abstract static class Parametrization {
		private final FloatClosedInterval parameterInterval;
		
		/**
		 * @post Crea la parametrización con el intervalo de parametrización
		 * 		 especificado
		 */
		protected Parametrization(FloatClosedInterval parameterInterval) {
			if ( parameterInterval != null ) {
				this.parameterInterval = parameterInterval;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el intervalo de parametrización
		 */
		public FloatClosedInterval getParameterInterval() {
			return this.parameterInterval;
		}
		
		/**
		 * @post Devuelve el punto correspondiente al parámetro
		 * 		 especificado
		 */
		public abstract Vector2f getPoint(float parameter);
	}
	
	
	/**
	 * @post Crea la curva
	 */
	Curve() {
		
	}
	
	/**
	 * @post Devuelve la parametrización
	 */
	public abstract Parametrization getParametrization();
	
	/**
	 * @post Procesa con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
