# Ecore-to-OMF Mapping

The list below describes the mapping of Ecore to OMF as wells some ***missing*** mappings between the two and  the solutions. 

1. **Problem**. There are some Ecore’s primitive types, such as EJavaObject, EJavaClass, and EEnumerator, that OML does not support.
   **Solution**. The temporary solution for this problem is to return `xsd:string` as the substitutions of these types. The ideal solution is to return the OML concepts of these types.

2. An `EClass` is mapped to a `Concept` in OML.    

3. An `Ecore metamodel` is mapped to a Vocabulary` in OML.

4. An `Ecore model` is mapped to a `Description` in OML.

5. An `EObject` is mapped to an `Instance` in OML.

6. An `EAttribute` is mapped to a `Property` in OML.

7. An `EEnum` is mapped to a `Scalar` in OML.

8. **Problem**. OML requires explicitly defining relations (edges, links) between concepts as `Relation`. It has no built-in support containment relationship. Some OML’s built-in relation characteristics are functional, inverse functional, symmetric, asymmetric, reflective, irreflexive, and transitive (see the documentation for details). 
   
   In Ecore, the relations are implicitly defined by `EReference`. The characteristics of the relationships can be containment or reference-only (non-containment), opposite (bi-directional), isMany, lowerBound, upperBound, etc.
   
   Ecore model references have to be transformed into relations in OML. The problem is in OML, relations are defined outside concepts. Thus, a relation can be re-used by two or more independent concepts. In EMF, two references with the same name and type but with different owners (EClass) are treated as two different things.
   
   Therefore, these references have to be represented as two different relations. However, using only the same name for the relations causes conflict as they are defined more than once.
   
   **Solution**. A naming mechanism should be developed to make unique names for the target relations. The unique-naming mechanism should also apply to the mapping of `EAttribute` to  `Property`,  as OML properties are also defined outside concepts; they can be reused in multiple concepts. For example:
   
   **OML Property**:
   
   `property.name = eClass.name  + "_" + eAttribute.name`
   
   **OML relation**:
   
   containment relationship
   
   `relation.name = eContainer.name + "_" + eReference.name + "_" + eObject.name`
   
   reverse containment relationship
   
   `relation.name = eObject.name + "_in_" + eReference.name + "_" + eContainer.name`
   
   non-containment relationship
   
   `relation.name = eObject1.name + "_" + eReference.name + "_" + eObject2.name`
   
   opposite relationship
   
   `relation.name = "rev_" + eObject2.name + "_" + eReference.name + "_" + eObject1.name`
   
   The `name` is the `name` property owned by the `eObject`. If an `eObject` doesn't have the property then the concatenation of the `eObject`'s class name and the index of the `eObject`  in the `eReference` is used.
