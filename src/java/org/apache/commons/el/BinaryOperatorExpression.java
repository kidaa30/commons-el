/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.Map;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.FunctionMapper;

/**
 *
 * <p>An expression representing a binary operator on a value
 * 
 * @author Nathan Abramson - Art Technology Group
 * @author Shawn Bayern
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: scolebourne $
 **/

public class BinaryOperatorExpression
  extends Expression
{
  //-------------------------------------
  // Properties
  //-------------------------------------
  // property expression

  Expression mExpression;
  public Expression getExpression ()
  { return mExpression; }
  public void setExpression (Expression pExpression)
  { mExpression = pExpression; }

  //-------------------------------------
  // property operators

  List mOperators;
  public List getOperators ()
  { return mOperators; }
  public void setOperators (List pOperators)
  { mOperators = pOperators; }

  //-------------------------------------
  // property expressions

  List mExpressions;
  public List getExpressions ()
  { return mExpressions; }
  public void setExpressions (List pExpressions)
  { mExpressions = pExpressions; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public BinaryOperatorExpression (Expression pExpression,
				   List pOperators,
				   List pExpressions)
  {
    mExpression = pExpression;
    mOperators = pOperators;
    mExpressions = pExpressions;
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
    buf.append (mExpression.getExpressionString ());
    for (int i = 0, size = mOperators.size(); i < size; i++) {
      BinaryOperator operator = (BinaryOperator) mOperators.get (i);
      Expression expression = (Expression) mExpressions.get (i);
      buf.append (" ");
      buf.append (operator.getOperatorSymbol ());
      buf.append (" ");
      buf.append (expression.getExpressionString ());
    }
    buf.append (")");

    return buf.toString ();
  }

  //-------------------------------------
  /**
   *
   * Evaluates to the literal value
   **/
  public Object evaluate (VariableResolver pResolver,
			  FunctionMapper functions)
    throws ELException
  {
    Object value = mExpression.evaluate (pResolver, functions);
    for (int i = 0, size = mOperators.size(); i < size; i++) {
      BinaryOperator operator = (BinaryOperator) mOperators.get (i);

      // For the And/Or operators, we need to coerce to a boolean
      // before testing if we shouldEvaluate
      if (operator.shouldCoerceToBoolean ()) {
	value = Coercions.coerceToBoolean (value);
      }

      if (operator.shouldEvaluate (value)) {
	Expression expression = (Expression) mExpressions.get (i);
	Object nextValue = expression.evaluate (pResolver,
						functions);

	value = operator.apply (value, nextValue);
      }
    }
    return value;
  }

  //-------------------------------------
}
