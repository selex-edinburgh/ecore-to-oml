/**
 * generated by Xtext 2.31.0
 */
package com.leonardo.lsaf.sadl.sadl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#getType <em>Type</em>}</li>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#getName <em>Name</em>}</li>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#isUnbounded <em>Unbounded</em>}</li>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#getLowerBound <em>Lower Bound</em>}</li>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#getUpperBound <em>Upper Bound</em>}</li>
 *   <li>{@link com.leonardo.lsaf.sadl.sadl.Instance#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance()
 * @model
 * @generated
 */
public interface Instance extends EObject
{
  /**
   * Returns the value of the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' reference.
   * @see #setType(Component)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_Type()
   * @model
   * @generated
   */
  Component getType();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#getType <em>Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' reference.
   * @see #getType()
   * @generated
   */
  void setType(Component value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Unbounded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unbounded</em>' attribute.
   * @see #setUnbounded(boolean)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_Unbounded()
   * @model
   * @generated
   */
  boolean isUnbounded();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#isUnbounded <em>Unbounded</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Unbounded</em>' attribute.
   * @see #isUnbounded()
   * @generated
   */
  void setUnbounded(boolean value);

  /**
   * Returns the value of the '<em><b>Lower Bound</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lower Bound</em>' attribute.
   * @see #setLowerBound(int)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_LowerBound()
   * @model
   * @generated
   */
  int getLowerBound();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#getLowerBound <em>Lower Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lower Bound</em>' attribute.
   * @see #getLowerBound()
   * @generated
   */
  void setLowerBound(int value);

  /**
   * Returns the value of the '<em><b>Upper Bound</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Upper Bound</em>' attribute.
   * @see #setUpperBound(int)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_UpperBound()
   * @model
   * @generated
   */
  int getUpperBound();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#getUpperBound <em>Upper Bound</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Upper Bound</em>' attribute.
   * @see #getUpperBound()
   * @generated
   */
  void setUpperBound(int value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see com.leonardo.lsaf.sadl.sadl.SadlPackage#getInstance_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link com.leonardo.lsaf.sadl.sadl.Instance#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

} // Instance
