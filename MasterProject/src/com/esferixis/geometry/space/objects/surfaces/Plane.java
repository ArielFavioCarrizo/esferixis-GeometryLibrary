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
package com.esferixis.geometry.space.objects.surfaces;

import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.space.objects.AffineHolomorphicGeometricObject;
import com.esferixis.geometry.space.objects.Line;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.ProportionalMatrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.ArraysExtra;

/**
 * Plano
 * 
 * Aclaración: Se toma como convención que la normal apunta hacia afuera del volumen
 * 			   acotado por el plano
 * 
 * @author ariel
 *
 */
public final class Plane extends AbstractSurface<Plane> implements AffineHolomorphicGeometricObject<Plane> {
	private final Vector3f referencePoint;
	private final Vector3f normal;
	
	/**
	 * @pre El punto de referencia y la normal no pueden ser nulos
	 * @post Crea un plano con el punto de referencia y la normal
	 * 		 especificados
	 * @throws NullPointerException
	 */
	public Plane(Vector3f referencePoint, Vector3f normal) {
		if ( ( referencePoint != null ) && ( normal != null ) ) {
			this.referencePoint = referencePoint;
			this.normal = normal;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see se3d.geometry.objects.SpaceSet#transform(math.Matrix4f)
	 */
	@Override
	public Plane transform(Matrix4f transformMatrix) {
		return new Plane(transformMatrix.transformPoint(this.referencePoint), transformMatrix.transformDirection(this.normal));
	}
	
	/**
	 * @post Devuelve una matriz de transformación arbitraria y unitaria de los
	 * 		 puntos del plano
	 */
	public Matrix4f arbritaryUnitMatrix() {
		/*
		 * Demostración
		 * 
		 * A = (a1, a2, a3)
		 * B = (b1, b2, b3)
		 * C = (c1, c2, c3)
		 * 
		 * A perpendicular B <=> A * B = 0
		 * a1 * b1 + a2 * b2 + a3 * b3 = 0
		 * 
		 * a3 * b3 = -a1 * b1 - a2 * b2
		 * b3 = (-a1 * b1 - a2 * b2) / a3
		 * 
		 * Si b1=b2=1
		 * 
		 * b3 = (-a1 - a2) / a3
		 * 
		 * Por cuestiones de precisión se permutan
		 * las coordenadas de tal manera que el módulo
		 * de a3 sea lo más parecido a 1.
		 * 
		 * |A x B| = |A| * |B| * sin(ang)
		 * Como A es perpendicular a B entonces sin(ang) = 1
		 * => |A x B| = |A| * |B|
		 * Como |C| = |B| => C = (A x B) / |A|
		 * 
		 * A x B = (a2 * b3 - b2 * a3, b1 * a3 - a1 * b3, a1 * b2 - b1 * a2)
		 * Como b1=b2
		 * A x B = (a2 * b3 - a3, a3 - a1 * b3, a1 - a2)
		 * 
		 * Si |A|=1 => C = (A x B)
		 */
		float a[] = this.normal.normalise().toArray();
		float b[] = new float[]{1.0f, 1.0f, 1.0f};
		float c[];
		
		float diffa1 = Math.abs(a[0] - 1.0f);
		float diffa2 = Math.abs(a[1] - 1.0f);
		float diffa3 = Math.abs(a[2] - 1.0f);
		
		int coordenadas[];
		if ( diffa1 < diffa2 ) {
			if ( diffa1 < diffa3 ) {
				coordenadas = new int[]{1, 2, 0};
			}
			else {
				coordenadas = new int[]{0, 1, 2};
			}
		}
		else {
			if ( diffa2 < diffa3 ) {
				coordenadas = new int[]{0, 2, 1};
			}
			else {
				coordenadas = new int[]{0, 1, 2};
			}
		}
		
		a = (float[]) ArraysExtra.permutatedOriginCopy(Float.TYPE, a, coordenadas);
		
		b[2] = (a[0] - a[1]) / a[2];
		c = new float[]{a[1] * b[2] - a[2], a[2] - a[0] * b[3], a[1] - a[2]};
		
		Vector3f localX = new Vector3f((float[]) ArraysExtra.permutatedDestinationCopy(Float.TYPE, b, coordenadas));
		Vector3f localY = new Vector3f((float[]) ArraysExtra.permutatedDestinationCopy(Float.TYPE, c, coordenadas));
		
		float length_inverse = 1.0f / ( (float)  Math.sqrt( localX.lengthSquared() ) );
		
		return new Matrix4f(localX.scale(length_inverse), localY.scale(length_inverse) );
	}
	
	/**
	 * @post Devuelve el punto de referencia
	 */
	public Vector3f getReferencePoint() {
		return this.referencePoint;
	}
	
	/**
	 * @post Devuelve la normal a la superfice
	 */
	public Vector3f getNormal() {
		return this.normal;
	}
	
	/* (non-Javadoc)
	 * @see se3d.geometry.objects.surfaces.Surface#isNormalSide(math.Vector3f)
	 */
	@Override
	public boolean isNormalSide(Vector3f point) {
		return (point.sub(this.referencePoint).dot(this.normal) >= 0.0f);
	}
	
	/**
	 * @post Devuelve el plano con la normal opuesta
	 */
	public Plane oppositePlane() {
		return new Plane(this.referencePoint, this.normal.opposite());
	}
	
	/**
	 * @post Devuelve la distancia normalizada, o sea en términos de
	 * 		 longitud del vector normal
	 */
	public float getNormalScaleDistance(Vector3f point) {
		return this.normal.scalarProjection( point.sub(this.referencePoint) );
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.surfaces.Surface#getIntersection(com.esferixis.geometry.objects.Line)
	 */
	@Override
	public List<Float> p_intersect(Line line) {
		float a = this.normal.dot(line.getReferencePoint());
		float b = this.normal.dot(line.getDirection());
		
		if ( a != 0.0f ) {
			return Collections.singletonList(-b / a);
		}
		else {
			return Collections.emptyList();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.SimpleSpaceSet#transform(com.esferixis.math.ProportionalMatrix4f)
	 */
	@Override
	public Plane transform(ProportionalMatrix4f transformMatrix) {
		return this.transform((Matrix4f) transformMatrix);
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject#accept(com.esferixis.geometry.objects.ProportionalHolomorphicGeometricObject.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.geometry.space.objects.ProportionalHolomorphicGeometricObject.Visitor<V> visitor) {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.geometry.objects.AffineHolomorphicGeometricObject#accept(com.esferixis.geometry.objects.AffineHolomorphicGeometricObject.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.geometry.space.objects.AffineHolomorphicGeometricObject.Visitor<V> visitor) {
		return visitor.visit(this);
	}
}
