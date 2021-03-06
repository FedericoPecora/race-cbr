/*******************************************************************************
 * Copyright (c) 2010-2013 Federico Pecora <federico.pecora@oru.se>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package spatial.rectangleAlgebra_OLD;

import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;




public class TwoDimensionsAllenConstraint extends QualitativeAllenIntervalConstraint {

	/**
	 * test
	 */
	private static final long serialVersionUID = -8168190298662846847L;
	private QualitativeAllenIntervalConstraint.Type[] con = new QualitativeAllenIntervalConstraint.Type[2];
	
	public TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type xcon, QualitativeAllenIntervalConstraint.Type ycon){
		this.con[0] = xcon;
		this.con[1] = ycon;
	}
	
	public QualitativeAllenIntervalConstraint.Type[] getAllenType() {
		return con;
	}
	
	@Override
	public String toString() {
		String ret = "[";
		for (int i = 0; i < types.length; i++) {
			ret +="(" + this.getFrom() + ") --" + "(" + this.con[0] + ", " + this.con[1] + ")" +"--> (" + this.getTo() + ")"; 
		}
		ret += "]";
		return ret;

	}
}




