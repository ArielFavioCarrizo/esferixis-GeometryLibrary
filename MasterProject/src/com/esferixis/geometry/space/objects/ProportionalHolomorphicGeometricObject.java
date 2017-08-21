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
package com.esferixis.geometry.space.objects;

import com.esferixis.geometry.space.objects.group.AffineHolomorphicGeometricObjectGroup;
import com.esferixis.geometry.space.objects.group.ProportionalHolomorphicGeometricObjectGroup;
import com.esferixis.geometry.space.objects.surfaces.Capsule;
import com.esferixis.geometry.space.objects.surfaces.InfiniteCylinder;
import com.esferixis.geometry.space.objects.surfaces.Plane;
import com.esferixis.geometry.space.objects.surfaces.Sphere;
import com.esferixis.geometry.space.objects.surfaces.Triangle;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.misc.classextra.ConcreteInstanceClassIndexer;

/**
 * Objeto geométrico holomórficamente transformable
 * por una matriz de transformación proporcional
 * 
 * @author ariel
 */
public interface ProportionalHolomorphicGeometricObject<T extends ProportionalHolomorphicGeometricObject<T>> {
	public interface Visitor<V> {
		public V visit(Edge edge);
		public V visit(Line line);
		public V visit(Point point);
		public V visit(Capsule capsule);
		public V visit(InfiniteCylinder infiniteCylinder);
		public V visit(Plane plane);
		public V visit(Sphere sphere);
		public V visit(Triangle triangle);
		public V visit(AffineHolomorphicGeometricObjectGroup affineGroup);
		public V visit(ProportionalHolomorphicGeometricObjectGroup proportionalGroup);
		public V visit(Fullness fullness);
	}
	
	/**
	 * @pre La matriz no puede ser nula
	 * @post Devuelve una transfomación del conjunto de puntos con la matriz de
	 * 		 transformación especificada
	 */
	public T transform(ProportionalMatrix4f transformMatrix);
	
	/**
	 * @post Visita el objeto geométrico con el visitor especificado
	 */
	public <V> V accept(Visitor<V> visitor);
	
	public static final ConcreteInstanceClassIndexer< ProportionalHolomorphicGeometricObject<?> > CONCRETEINSTANCECLASSINDEXER = new ConcreteInstanceClassIndexer< ProportionalHolomorphicGeometricObject<?> >( (Class< ProportionalHolomorphicGeometricObject<?> >) (Class<?>) AffineHolomorphicGeometricObject.class, 11) {
		
		@Override
		public int classIndex(ProportionalHolomorphicGeometricObject<?> element) {
			return element.accept(new Visitor<Integer>() {

				@Override
				public Integer visit(Edge edge) {
					return 0;
				}

				@Override
				public Integer visit(Line line) {
					return 1;
				}

				@Override
				public Integer visit(Point point) {
					return 2;
				}

				@Override
				public Integer visit(Capsule capsule) {
					return 3;
				}

				@Override
				public Integer visit(InfiniteCylinder infiniteCylinder) {
					return 4;
				}

				@Override
				public Integer visit(Plane plane) {
					return 5;
				}

				@Override
				public Integer visit(Sphere sphere) {
					return 6;
				}

				@Override
				public Integer visit(Triangle triangle) {
					return 7;
				}

				@Override
				public Integer visit(
						AffineHolomorphicGeometricObjectGroup affineGroup) {
					return 8;
				}

				@Override
				public Integer visit(
						ProportionalHolomorphicGeometricObjectGroup proportionalGroup) {
					return 9;
				}

				@Override
				public Integer visit(Fullness fullness) {
					return 10;
				}
				
			});
		}
		
	};
			
}
