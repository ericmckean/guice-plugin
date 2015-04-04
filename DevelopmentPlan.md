## Status ##

The plugin is in **alpha** release of the Eclipse version for internal use at Google.

The IntelliJ plugin will be built shortly.

After both have been in use at Google for a while, the project will move to beta phase and be made public.

## Big Picture ##

  * **Phase I** Navigation
  * **Phase II** Problem Notification
  * **Phase III** Problem Correction Assistance
  * **Phase IV** Refactoring

### Phase I. Navigation ###

Allow users to navigate guice code by jumping through bindings.

**Status**: 90% complete.  The "Find Bindings" feature is done (except for a few outstanding issues).

**Outstanding Issues**
  * IntelliJ version

**Eclipse Version Outstanding Issues**
  * Determine if element is at an injection point and its annotations (requires update to Eclipse to be practical)

**Tools Suite Outstanding Issues**
  * Implement the Module taking arguments functionality
  * Write sample code

### Phase II. Problem Notification ###

Notify users of guice related problems in their code (e.g. CreationException) with markers (red and yellow underlining etc).

**Status**: 25% complete.  The back end is in place, the ProblemsHandler object receives information from the code runs about any problems detected.  These are passed in the form of CodeProblem objects which wrap the underlying exceptions that will be thrown at runtime.

**Plan**
  1. Complete the implementation of the Problems API
  1. Implement code listening (IDE specific)
  1. Implement the user interface

### Phase III. Problem Correction ###

Use Code Complete features of IDEs to help the users correct problems from Phase II (such as alt-enter in IntelliJ to auto suggest code).

**Status**: Not started.

**Plan**
  1. Decide on supported problems
  1. Design a code problem correction framework for the plugin
  1. Implement the framework
  1. Implement the supported problems
  1. Implement the user interface

### Phase IV. Refactoring ###

Allow users to refactor guice code automatically, for instance "add injected parameter" to a constructor.

**Status**: Not started.

**Plan**
  1. Decide on supported refactorings
  1. Design a refactoring framework for the plugin
  1. Implement the refactoring framework
  1. Implement the refactorings
  1. Implement the user interface





