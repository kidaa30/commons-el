/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.el;

import java.util.List;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

/**
 *
 * <p>An expression representing one or more unary operators on a
 * value
 * 
 * @author Nathan Abramson - Art Technology Group
 * @author Shawn Bayern
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 **/

public class UnaryOperatorExpression
  extends Expression
{
  //-------------------------------------
  // Properties
  //-------------------------------------
  // property operator

  UnaryOperator mOperator;
  public UnaryOperator getOperator ()
  { return mOperator; }
  public void setOperator (UnaryOperator pOperator)
  { mOperator = pOperator; }

  //-------------------------------------
  // property operators

  List mOperators;
  public List getOperators ()
  { return mOperators; }
  public void setOperators (List pOperators)
  { mOperators = pOperators; }

  //-------------------------------------
  // property expression

  Expression mExpression;
  public Expression getExpression ()
  { return mExpression; }
  public void setExpression (Expression pExpression)
  { mExpression = pExpression; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public UnaryOperatorExpression (UnaryOperator pOperator,
				  List pOperators,
				  Expression pExpression)
  {
    mOperator = pOperator;
    mOperators = pOperators;
    mExpression = pExpression;
  }

  //-------------------------------------
  // Expression methods
  //-------------------------------------
  /**
   *
   * Returns the expression in the expression language syntax
   **/
  public String getExpressionString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append ("(");
    if (mOperator != null) {
      buf.append (mOperator.getOperatorSymbol ());
      buf.append (" ");
    }
    else {
      for (int i = 0, size = mOperators.size(); i < size; i++) {
	UnaryOperator operator = (UnaryOperator) mOperators.get (i);
	buf.append (operator.getOperatorSymbol ());
	buf.append (" ");
      }
    }
    buf.append (mExpression.getExpressionString ());
    buf.append (")");
    return buf.toString ();
  }

  //-------------------------------------
  /**
   *
   * Evaluates to the literal value
   **/
  public Object evaluate (VariableResolver pResolver, FunctionMapper functions)
    throws ELException
  {
    Object value = mExpression.evaluate (pResolver, functions);
    if (mOperator != null) {
      value = mOperator.apply (value);
    }
    else {
      for (int i = mOperators.size () - 1; i >= 0; i--) {
	UnaryOperator operator = (UnaryOperator) mOperators.get (i);
	value = operator.apply (value);
      }
    }
    return value;
  }

  public Expression bindFunctions(final FunctionMapper functions) throws ELException {
      return new UnaryOperatorExpression(
              mOperator,
              mOperators,
              mExpression.bindFunctions(functions));
  }

  //-------------------------------------
}
