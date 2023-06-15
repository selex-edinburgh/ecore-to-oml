# Comparison of Old vs New Generators

## Old Generator
```java
vocabulary <http://www.leonardo.com/lsaf/sadl/SADL#> as sADL {
	extends <http://www.w3.org/2000/01/rdf-schema#> as rdfs
	extends <http://www.w3.org/2001/XMLSchema#> as xsd

	@rdfs:label "Model"
	concept Model

	relation entity Model_package_A [
		from Model
		to Package
		@rdfs:label "package"
		forward Model_package
	]

  @rdfs:label "Package"
	concept Package

	@rdfs:label "name"
	scalar property Package_name [
		domain Package
		range xsd:string
		functional
	]

    ...

    @rdfs:label "Unit"
	concept Unit :> PackageableElement

	relation entity Unit_group_A [
		from Unit
		to Unit
		@rdfs:label "group"
		forward Unit_group
		functional
	]
```

## New Generator
```java
vocabulary <http://www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL#> as sADL {
  extends <http://www.w3.org/2000/01/rdf-schema#> as rdfs
  extends <http://www.w3.org/2001/XMLSchema#> as xsd
  extends <http://www.leonardo.com/lsaf/sadl/SADL/vocabulary/base#> as base

  @rdfs:comment ""
  concept Model < base:IdentifiedThing

  @rdfs:comment ""
  relation entity Model_package_Package [
    from Model
    to Package
    forward model_package_package
    reverse package_in_package_model
    symmetric        
    irreflexive
    transitive  
  ]

  @rdfs:comment ""
  concept Package < base:IdentifiedThing

  @rdfs:comment ""
  scalar property package_name [
    domain Package
    range xsd:string
    functional
  ]
  @rdfs:comment ""
  scalar property package_description [
    domain Package
    range xsd:string
    functional
  ]

  ...

   @rdfs:comment ""
  concept Unit < base:IdentifiedThing, PackageableElement

  @rdfs:comment ""
  scalar property unit_description [
    domain Unit
    range xsd:string
    functional
  ]
```
## Comparison

1. The new generator includes a `base` concept to include, in the future, common/other concepts related to Ecore but are not defined automatically by the model/metamodel. That is why we can find any concept extended from `base:IdentifiedThing`, for example, `concept Model < base:IdentifiedThing` in the new generator.
2. The second generator also considers the attributes (`eOpposite`, `isMany`, etc.) of a feature in Ecore. Thus, it can be more specific in defining relations, such as `reverse`, `symmetric`, `irreflexive`, `transitive`, etc., which cannot be found in the old generator. 
3. The old generator adds an `A` character at the end of a relation name `Model_package_A`. The first segment is the class name of the container, the second segment is the name of the reference, and the last segment is the `A`. The new generator replaces the A with the type of the reference, which is `Model_package_Package`.
4. The output syntax of the old generator is outdated since it breaks some conventions of expressions. For example, the old generator expresses the first character of a `scalar property` in **uppercase**, but the new format expresses it in **lowercase**, and the old generator expresses **specialisation axiom** as `:>`, while the new format expresses the specialisation as `<`.
