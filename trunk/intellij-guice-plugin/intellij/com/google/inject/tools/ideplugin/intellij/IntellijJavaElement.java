package com.google.inject.tools.ideplugin.intellij;

import com.intellij.psi.*;
import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.suite.JavaManager;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: d
 * Date: Sep 21, 2007
 * Time: 11:15:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntellijJavaElement implements JavaElement {
  private final PsiElement element;
  private Type type;
  private String className;
  private boolean isConcrete;
  private String annotations;
  private boolean isInjectionPoint;

  public IntellijJavaElement(PsiElement element) {
    this.element = element;
    type = null;
    className = null;
    isConcrete = false;
    annotations = null;
    isInjectionPoint = false;
    element.accept(new MyJavaElementVisitor());
  }

  public Type getType() {
    return type;
  }

  public String getClassName() {
    return className;
  }

  public String getName() {
    return element.toString();
  }

  public JavaManager getJavaProject() {
    return new IntellijJavaProject(element.getProject());
  }

  public boolean isInjectionPoint() {
    return isInjectionPoint;
  }

  public String getAnnotations() {
    return annotations;
  }

  public boolean isConcreteClass() {
    return isConcrete;
  }

  class MyJavaElementVisitor extends PsiElementVisitor {
    public void visitClass(PsiClass psiClass) {
      super.visitClass(psiClass);
      if (IntellijJavaElement.this.type == null) IntellijJavaElement.this.type = Type.FIELD;
      IntellijJavaElement.this.className = psiClass.getQualifiedName();
      IntellijJavaElement.this.isConcrete = !(psiClass.isAnnotationType() || psiClass.isInterface());
    }

    public void visitClassInitializer(PsiClassInitializer psiClassInitializer) {
      super.visitClassInitializer(psiClassInitializer);
      visitMember(psiClassInitializer);
    }

    public void visitClassObjectAccessExpression(PsiClassObjectAccessExpression psiClassObjectAccessExpression) {
      super.visitClassObjectAccessExpression(psiClassObjectAccessExpression);
      visitTypeElement(psiClassObjectAccessExpression.getOperand());
    }

    public void visitDeclarationStatement(PsiDeclarationStatement psiDeclarationStatement) {
      super.visitDeclarationStatement(psiDeclarationStatement);
      if (psiDeclarationStatement.getDeclaredElements().length > 0) {
        psiDeclarationStatement.getDeclaredElements()[0].accept(this);
      }
    }

    public void visitField(PsiField psiField) {
      super.visitField(psiField);
      IntellijJavaElement.this.type = Type.FIELD;
      visitType(psiField.getType());
    }

    public void visitLocalVariable(PsiLocalVariable psiLocalVariable) {
      super.visitLocalVariable(psiLocalVariable);
      visitVariable(psiLocalVariable);
    }

    public void visitMember(PsiMember psiMember) {
      visitClass(psiMember.getContainingClass());
    }

    public void visitParameter(PsiParameter psiParameter) {
      super.visitParameter(psiParameter);
      visitTypeElement(psiParameter.getTypeElement());
    }

    public void visitParameterList(PsiParameterList psiParameterList) {
      super.visitParameterList(psiParameterList);
      if (psiParameterList.getParameters().length > 0) {
        visitParameter(psiParameterList.getParameters()[0]);
      }
    }

    public void visitReferenceElement(PsiJavaCodeReferenceElement psiJavaCodeReferenceElement) {
      super.visitReferenceElement(psiJavaCodeReferenceElement);
      if (psiJavaCodeReferenceElement.getTypeParameters().length > 0) {
        visitType(psiJavaCodeReferenceElement.getTypeParameters()[0]);
      }
    }

    public void visitReferenceExpression(PsiReferenceExpression psiReferenceExpression) {
      PsiReference reference = psiReferenceExpression.getReference();
      if (reference != null) {
        PsiElement resolved = reference.resolve();
        if (resolved != null) resolved.accept(this);
      }
    }

    public void visitTypeParameterList(PsiTypeParameterList psiTypeParameterList) {
      super.visitTypeParameterList(psiTypeParameterList);
      if (psiTypeParameterList.getTypeParameters().length > 0) {
        visitTypeParameter(psiTypeParameterList.getTypeParameters()[0]);
      }
    }

    public void visitTypeElement(PsiTypeElement psiTypeElement) {
      super.visitTypeElement(psiTypeElement);
      visitType(psiTypeElement.getType());
    }

    public void visitTypeCastExpression(PsiTypeCastExpression psiTypeCastExpression) {
      super.visitTypeCastExpression(psiTypeCastExpression);
      visitTypeElement(psiTypeCastExpression.getCastType());
    }

    public void visitVariable(PsiVariable psiVariable) {
      super.visitVariable(psiVariable);
      IntellijJavaElement.this.type = Type.FIELD;
      visitType(psiVariable.getType());
    }

    public void visitJavaFile(PsiJavaFile psiJavaFile) {
      super.visitJavaFile(psiJavaFile);
      if (psiJavaFile.getClasses().length > 0) {
        visitClass(psiJavaFile.getClasses()[0]);
      }
    }

    public void visitImplicitVariable(ImplicitVariable implicitVariable) {
      super.visitImplicitVariable(implicitVariable);
      visitVariable(implicitVariable);
    }
  }

  public void visitType(PsiType type) {
    if (this.type == null) this.type = Type.PARAMETER;
    this.className = type.accept(new MyTypeVisitor());
  }

  class MyTypeVisitor extends PsiTypeVisitor<String> {
    public String visitType(PsiType psiType) {
      return null;
    }

    public String visitPrimitiveType(PsiPrimitiveType psiPrimitiveType) {
      IntellijJavaElement.this.isConcrete = true;
      return psiPrimitiveType.getCanonicalText();
    }

    public String visitArrayType(PsiArrayType psiArrayType) {
      return psiArrayType.getComponentType().accept(this);
    }

    public String visitClassType(PsiClassType psiClassType) {
      IntellijJavaElement.this.isConcrete = !(psiClassType.resolve().isAnnotationType() || psiClassType.resolve().isInterface());
      return psiClassType.getClassName();
    }

    public String visitCapturedWildcardType(PsiCapturedWildcardType psiCapturedWildcardType) {
      return psiCapturedWildcardType.getUpperBound().accept(this);
    }

    public String visitWildcardType(PsiWildcardType psiWildcardType) {
      return psiWildcardType.getSuperBound().accept(this);
    }

    public String visitEllipsisType(PsiEllipsisType psiEllipsisType) {
      return psiEllipsisType.toArrayType().accept(this);
    }
  }
}
