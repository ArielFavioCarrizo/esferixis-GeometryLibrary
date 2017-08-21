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
import java.util.Arrays;
import java.util.Collections;

import com.esferixis.math.Vector3f;
import com.esferixis.misc.collection.list.ListDecorator;

public class VerticesList extends ListDecorator<Vector3f> {
	
	private Vector3f center;
	
	public VerticesList(Vector3f... vertices) {
		super( Collections.unmodifiableList( Arrays.asList(vertices.clone()) ) );
		this.center = null;
	}
	
	/**
	 * @post Devuelve el centro del grupo de vértices
	 */
	public Vector3f getCenter() {
		if ( this.center == null ) {
			this.center = new Vector3f(0.0f, 0.0f, 0.0f);
			for ( Vector3f eachVertex : this ) {
				this.center.add(eachVertex);
			}
			this.center.scale( 1.0f / this.size() );
		}
		return this.center;
	}
	
	/**
	 * @post Devuelve los vértices opuestos
	 */
	public VerticesList opposite() {
		Vector3f oppositeVertices[] = new Vector3f[this.size()];
		int i=0;
		for ( Vector3f eachVertex : this ) {
			oppositeVertices[i++] = eachVertex.opposite();
		}
		return new VerticesList(oppositeVertices);
	}
	
	/**
	 * @post Desplaza los vértices con el vector especificado
	 */
	public VerticesList displace(Vector3f displacement) {
		Vector3f oppositeVertices[] = new Vector3f[this.size()];
		int i=0;
		for ( Vector3f eachVertex : this ) {
			oppositeVertices[i++] = eachVertex.add(displacement);
		}
		return new VerticesList(oppositeVertices);
	}
	
}
