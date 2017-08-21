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
package com.esferixis.geometry.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.collection.list.BinaryList;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Caja alineada a los ejes
 * 
 * @author ariel
 *
 */
public final class AxisAlignedBox {
	private final Vector3f minPoint, maxPoint;
	
	/**
	 * @post Crea una caja envolvente con los puntos de la diagonal especificada
	 * 		 y guarda la diagonal con p0 con los componentes menores y p1
	 * 		 con los componentes mayores correspondientes a cada coordenada
	 * 		 de la diagonal especificada
	 */
	public AxisAlignedBox(Vector3f p0, Vector3f p1) {
		this.minPoint = new Vector3f(
				Math.min(p0.getX(), p1.getX()),
				Math.min(p0.getY(), p1.getY()),
				Math.min(p0.getZ(), p1.getZ())
		);
		this.maxPoint = new Vector3f(
				Math.max(p0.getX(), p1.getX()),
				Math.max(p0.getY(), p1.getY()),
				Math.max(p0.getZ(), p1.getZ())
		);
	}
	
	/**
	 * @post Devuelve la dilatación con la caja especificada
	 */
	public AxisAlignedBox dilation(AxisAlignedBox other) {
		return new AxisAlignedBox( minPoint.add(other.minPoint), maxPoint.add(other.maxPoint) );
	}
	
	private static BinaryList<Float> intervalIntersection(BinaryList<Float> interval1, BinaryList<Float> interval2) {
		float min = Math.max(interval1.get(0), interval2.get(0));
		float max = Math.min(interval1.get(1), interval2.get(1));
		
		final BinaryList<Float> result;
		if ( min >= max ) {
			result = new BinaryList<Float>(min, max);
		}
		else {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * @post Devuelve la intersección con la caja especificada
	 */
	public AxisAlignedBox intersect(AxisAlignedBox other) {
		final AxisAlignedBox result;
		
		final BinaryList<Float> xInterval = intervalIntersection( new BinaryList<Float>(this.minPoint.getX(), this.maxPoint.getX()), new BinaryList<Float>(other.minPoint.getX(), other.maxPoint.getX()) );
		final BinaryList<Float> yInterval = intervalIntersection( new BinaryList<Float>(this.minPoint.getY(), this.maxPoint.getY()), new BinaryList<Float>(other.minPoint.getY(), other.maxPoint.getY()) );
		final BinaryList<Float> zInterval = intervalIntersection( new BinaryList<Float>(this.minPoint.getZ(), this.maxPoint.getZ()), new BinaryList<Float>(other.minPoint.getZ(), other.maxPoint.getZ()) );
		
		if ( ( xInterval != null ) && ( yInterval != null ) && ( zInterval != null ) ) {
			result = new AxisAlignedBox( new Vector3f( xInterval.get(0), yInterval.get(0), zInterval.get(0) ), new Vector3f( xInterval.get(1), yInterval.get(1), zInterval.get(1) ) );
		}
		else {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * @post Devuelve la intersección entre las cajas especificadas
	 */
	public static AxisAlignedBox intersectAll(Collection<AxisAlignedBox> boxes) {
		AxisAlignedBox remaining = null;
		for ( AxisAlignedBox eachAxisAlignedBox : boxes ) {
			if ( remaining != null ) {
				remaining = remaining.intersect(eachAxisAlignedBox);
			}
			else {
				remaining = eachAxisAlignedBox;
			}
		}
		return remaining;
	}
	
	/**
	 * @post Crea una caja si los valores especificados son consistentes,
	 * 		 caso contrario devuelve null
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param minZ
	 * @param maxZ
	 * @return
	 */
	private static AxisAlignedBox createIfConsistent(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
		final AxisAlignedBox newAABB;
		if ( ( minX >= maxX ) && ( minY >= maxY ) && ( minZ >= maxZ ) ) {
			newAABB = new AxisAlignedBox( new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ) );
		}
		else {
			newAABB = null;
		}
		return newAABB;
	}
	
	/**
	 * @post Devuelve las cajas que componen la sustracción con la caja especificada
	 */
	public Collection<AxisAlignedBox> sub(AxisAlignedBox subtrahend) {
		/**
		 * Los casos de intersección del substrahendo con el minuendo son los siguientes
		 * 
		 * - No hay intersección
		 * - Cubre una sola esquina
		 * - Cubre parcialmente una sola arista sin cubrir esquinas
		 * - Cubre parcialmente dos aristas paralelas sin cubrir esquinas
		 * - Cubre completamente una sola arista
		 * - Cubre parcialmente una cara sin cubrir aristas
		 * - Cubre parcialmente una cara y su paralela sin cubrir aristas
		 * - Cubre completamente una cara
		 * - Cubre todo
		 */
		
		Collection<AxisAlignedBox> resultAABBs = new ArrayList<AxisAlignedBox>(6);
		
		float[][] candidateAABBs = new float[][]{
			new float[]{
					this.minPoint.getX(), subtrahend.minPoint.getX(),
					this.minPoint.getY(), this.maxPoint.getY(),
					this.minPoint.getZ(), this.maxPoint.getZ()
			},
			new float[]{
					subtrahend.minPoint.getX(), subtrahend.maxPoint.getX(),
					this.minPoint.getY(), this.maxPoint.getY(),
					this.minPoint.getZ(), subtrahend.minPoint.getZ()
			},
			new float[]{
					subtrahend.minPoint.getX(), subtrahend.maxPoint.getX(),
					this.minPoint.getY(), subtrahend.minPoint.getY(),
					subtrahend.minPoint.getZ(), subtrahend.maxPoint.getZ()
			},
			new float[]{
					subtrahend.minPoint.getX(), subtrahend.maxPoint.getX(),
					subtrahend.maxPoint.getY(), this.maxPoint.getY(),
					subtrahend.minPoint.getZ(), subtrahend.maxPoint.getZ()
			},
			new float[]{
					subtrahend.minPoint.getX(), subtrahend.maxPoint.getX(),
					this.minPoint.getY(), this.maxPoint.getY(),
					subtrahend.maxPoint.getZ(), this.maxPoint.getZ()
			},
			new float[]{
					subtrahend.maxPoint.getX(), this.maxPoint.getX(),
					this.minPoint.getY(), this.maxPoint.getY(),
					this.minPoint.getZ(), this.maxPoint.getZ()
			}
		};
		
		for ( float[] eachCandidate : candidateAABBs ) {
			AxisAlignedBox newAABB = createIfConsistent(
					eachCandidate[0], eachCandidate[1],
					eachCandidate[2], eachCandidate[3],
					eachCandidate[4], eachCandidate[5]
			);
			if ( newAABB != null ) {
				resultAABBs.add( newAABB );
			}
		}
		
		// Si están los 6 cubos
		if ( resultAABBs.size() == 6 ) {
			// Entonces no hay intersección con el sustrahendo, queda igual
			resultAABBs.clear();
			resultAABBs.add(this);
		}
		
		return resultAABBs;
	}
	
	/**
	 * @post Devuelve la resta entre las cajas que componen el minuendo y las que componen el sustraendo
	 */
	public static Collection<AxisAlignedBox> subAll(Collection<AxisAlignedBox> minuends, Collection<AxisAlignedBox> subtrahends) {
		Collection<AxisAlignedBox> remaining = new ArrayList<AxisAlignedBox>(minuends);
		
		for ( AxisAlignedBox eachSubtrahend : subtrahends ) {
			Collection<AxisAlignedBox> newRemaining = new ArrayList<AxisAlignedBox>();
			
			for ( AxisAlignedBox eachMinuend : remaining ) {
				newRemaining.addAll( eachMinuend.sub(eachSubtrahend) );
			}
			
			remaining = newRemaining;
		}
		
		return remaining;
	}
	
	/**
	 * @post Devuelve el vértice con los componentes menores
	 */
	public Vector3f getMinPoint() {
		return this.minPoint;
	}
	
	/**
	 * @post Devuelve el vértice con los componentes mayores
	 */
	public Vector3f getMaxPoint() {
		return this.maxPoint;
	}
}
