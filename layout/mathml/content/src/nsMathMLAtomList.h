/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Mozilla MathML Project.
 *
 * The Initial Developer of the Original Code is The University Of
 * Queensland.  Portions created by The University Of Queensland are
 * Copyright (C) 1999 The University Of Queensland.  All Rights Reserved.
 *
 * Contributor(s):
 *   Roger B. Sidje <rbs@maths.uq.edu.au>
 *   (Following the model of the Gecko team)
 */

/******

  This file contains the list of all MathML nsIAtoms and their values

  It is designed to be used as inline input to nsMathMLAtoms.cpp *only*
  through the magic of C preprocessing.

  All entires must be enclosed in the macro MATHML_ATOM which will have cruel
  and unusual things done to it

  It is recommended (but not strictly necessary) to keep all entries
  in alphabetical order

  The first argument to MATHML_ATOM is the C++ identifier of the atom
  The second argument is the string value of the atom

 ******/

MATHML_ATOM(abs_, "abs")
MATHML_ATOM(accent_, "accent")
MATHML_ATOM(accentunder_, "accentunder")
MATHML_ATOM(and_, "and")
MATHML_ATOM(annotation_, "annotation")
MATHML_ATOM(apply_, "apply")
MATHML_ATOM(arccos_, "arccos")
MATHML_ATOM(arcsin_, "arcsin")
MATHML_ATOM(arctan_, "arctan")
MATHML_ATOM(bvar_, "bvar")
MATHML_ATOM(ci_, "ci")
MATHML_ATOM(close_, "close")
MATHML_ATOM(cn_, "cn")
MATHML_ATOM(compose_, "compose")
MATHML_ATOM(condition_, "condition")
MATHML_ATOM(conjugate_, "conjugate")
MATHML_ATOM(cos_, "cos")
MATHML_ATOM(cosh_, "cosh")
MATHML_ATOM(cot_, "cot")
MATHML_ATOM(coth_, "coth")
MATHML_ATOM(csc_, "csc")
MATHML_ATOM(csch_, "csch")
MATHML_ATOM(declare_, "declare")
MATHML_ATOM(degree_, "degree")
MATHML_ATOM(determinant_, "determinant")
MATHML_ATOM(diff_, "diff")
MATHML_ATOM(displaystyle_, "displaystyle")
MATHML_ATOM(divide_, "divide")
MATHML_ATOM(eq_, "eq")
MATHML_ATOM(exists_, "exists")
MATHML_ATOM(exp_, "exp")
MATHML_ATOM(factorial_, "factorial")
MATHML_ATOM(fence_, "fence")
MATHML_ATOM(fn_, "fn")
MATHML_ATOM(fontstyle_, "fontstyle")
MATHML_ATOM(forall_, "forall")
MATHML_ATOM(form_, "form")
MATHML_ATOM(geq_, "geq")
MATHML_ATOM(gt_, "gt")
MATHML_ATOM(ident_, "ident")
MATHML_ATOM(implies_, "implies")
MATHML_ATOM(in_, "in")
MATHML_ATOM(int_, "int")
MATHML_ATOM(intersect_, "intersect")
MATHML_ATOM(interval_, "interval")
MATHML_ATOM(inverse_, "inverse")
MATHML_ATOM(lambda_, "lambda")
MATHML_ATOM(largeop_, "largeop")
MATHML_ATOM(leq_, "leq")
MATHML_ATOM(limit_, "limit")
MATHML_ATOM(linethickness_, "linethickness")
MATHML_ATOM(list_, "list")
MATHML_ATOM(ln_, "ln")
MATHML_ATOM(log_, "log")
MATHML_ATOM(logbase_, "logbase")
MATHML_ATOM(lowlimit_, "lowlimit")
MATHML_ATOM(lt_, "lt")
MATHML_ATOM(maction_, "maction")
MATHML_ATOM(maligngroup_, "maligngroup")
MATHML_ATOM(malignmark_, "malignmark")
MATHML_ATOM(math, "math") // the only one without an underscore
MATHML_ATOM(matrix_, "matrix")
MATHML_ATOM(matrixrow_, "matrixrow")
MATHML_ATOM(max_, "max")
MATHML_ATOM(mean_, "mean")
MATHML_ATOM(median_, "median")
MATHML_ATOM(merror_, "merror")
MATHML_ATOM(mfenced_, "mfenced")
MATHML_ATOM(mfrac_, "mfrac")
MATHML_ATOM(mi_, "mi")
MATHML_ATOM(min_, "min")
MATHML_ATOM(minus_, "minus")
MATHML_ATOM(mmultiscripts_, "mmultiscripts")
MATHML_ATOM(mn_, "mn")
MATHML_ATOM(mo_, "mo")
MATHML_ATOM(mode_, "mode")
MATHML_ATOM(moment_, "moment")
MATHML_ATOM(movablelimits_, "movablelimits")
MATHML_ATOM(mover_, "mover")
MATHML_ATOM(mpadded_, "mpadded")
MATHML_ATOM(mphantom_, "mphantom")
MATHML_ATOM(mprescripts_, "mprescripts")
MATHML_ATOM(mroot_, "mroot")
MATHML_ATOM(mrow_, "mrow")
MATHML_ATOM(ms_, "ms")
MATHML_ATOM(mspace_, "mspace")
MATHML_ATOM(msqrt_, "msqrt")
MATHML_ATOM(mstyle_, "mstyle")
MATHML_ATOM(msub_, "msub")
MATHML_ATOM(msubsup_, "msubsup")
MATHML_ATOM(msup_, "msup")
MATHML_ATOM(mtable_, "mtable")
MATHML_ATOM(mtd_, "mtd")
MATHML_ATOM(mtext_, "mtext")
MATHML_ATOM(mtr_, "mtr")
MATHML_ATOM(munder_, "munder")
MATHML_ATOM(munderover_, "munderover")
MATHML_ATOM(neq_, "neq")
MATHML_ATOM(none_, "none")
MATHML_ATOM(not_, "not")
MATHML_ATOM(notin_, "notin")
MATHML_ATOM(notprsubset_, "notprsubset")
MATHML_ATOM(notsubset_, "notsubset")
MATHML_ATOM(open_, "open")
MATHML_ATOM(or_, "or")
MATHML_ATOM(partialdiff_, "partialdiff")
MATHML_ATOM(plus_, "plus")
MATHML_ATOM(power_, "power")
MATHML_ATOM(product_, "product")
MATHML_ATOM(prsubset_, "prsubset")
MATHML_ATOM(quotient_, "quotient")
MATHML_ATOM(reln_, "reln")
MATHML_ATOM(rem_, "rem")
MATHML_ATOM(root_, "root")
MATHML_ATOM(scriptlevel_, "scriptlevel")
MATHML_ATOM(scriptminsize_, "scriptminsize")
MATHML_ATOM(scriptsizemultiplier_, "scriptsizemultiplier")
MATHML_ATOM(sdev_, "sdev")
MATHML_ATOM(sec_, "sec")
MATHML_ATOM(sech_, "sech")
MATHML_ATOM(select_, "select")
MATHML_ATOM(semantics_, "semantics")
MATHML_ATOM(sep_, "sep")
MATHML_ATOM(separator_, "separator")
MATHML_ATOM(separators_, "separators")
MATHML_ATOM(set_, "set")
MATHML_ATOM(setdiff_, "setdiff")
MATHML_ATOM(sin_, "sin")
MATHML_ATOM(sinh_, "sinh")
MATHML_ATOM(stretchy_, "stretchy")
MATHML_ATOM(subscriptshift_, "subscriptshift")
MATHML_ATOM(superscriptshift_, "superscriptshift")
MATHML_ATOM(subset_, "subset")
MATHML_ATOM(sum_, "sum")
MATHML_ATOM(symmetric_, "symmetric")
MATHML_ATOM(tan_, "tan")
MATHML_ATOM(tanh_, "tanh")
MATHML_ATOM(tendsto_, "tendsto")
MATHML_ATOM(times_, "times")
MATHML_ATOM(transpose_, "transpose")
MATHML_ATOM(union_, "union")
MATHML_ATOM(uplimit_, "uplimit")
MATHML_ATOM(var_, "var")
MATHML_ATOM(vector_, "vector")
MATHML_ATOM(xor_, "xor")
