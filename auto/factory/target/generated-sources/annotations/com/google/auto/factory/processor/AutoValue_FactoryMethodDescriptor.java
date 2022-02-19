package com.google.auto.factory.processor;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;
import javax.lang.model.type.TypeMirror;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_FactoryMethodDescriptor extends FactoryMethodDescriptor {

  private final AutoFactoryDeclaration declaration;

  private final String name;

  private final TypeMirror returnType;

  private final boolean publicMethod;

  private final boolean overridingMethod;

  private final ImmutableSet<Parameter> passedParameters;

  private final ImmutableSet<Parameter> providedParameters;

  private final ImmutableSet<Parameter> creationParameters;

  private final boolean isVarArgs;

  private final ImmutableSet<TypeMirror> exceptions;

  private AutoValue_FactoryMethodDescriptor(
      AutoFactoryDeclaration declaration,
      String name,
      TypeMirror returnType,
      boolean publicMethod,
      boolean overridingMethod,
      ImmutableSet<Parameter> passedParameters,
      ImmutableSet<Parameter> providedParameters,
      ImmutableSet<Parameter> creationParameters,
      boolean isVarArgs,
      ImmutableSet<TypeMirror> exceptions) {
    this.declaration = declaration;
    this.name = name;
    this.returnType = returnType;
    this.publicMethod = publicMethod;
    this.overridingMethod = overridingMethod;
    this.passedParameters = passedParameters;
    this.providedParameters = providedParameters;
    this.creationParameters = creationParameters;
    this.isVarArgs = isVarArgs;
    this.exceptions = exceptions;
  }

  @Override
  AutoFactoryDeclaration declaration() {
    return declaration;
  }

  @Override
  String name() {
    return name;
  }

  @Override
  TypeMirror returnType() {
    return returnType;
  }

  @Override
  boolean publicMethod() {
    return publicMethod;
  }

  @Override
  boolean overridingMethod() {
    return overridingMethod;
  }

  @Override
  ImmutableSet<Parameter> passedParameters() {
    return passedParameters;
  }

  @Override
  ImmutableSet<Parameter> providedParameters() {
    return providedParameters;
  }

  @Override
  ImmutableSet<Parameter> creationParameters() {
    return creationParameters;
  }

  @Override
  boolean isVarArgs() {
    return isVarArgs;
  }

  @Override
  ImmutableSet<TypeMirror> exceptions() {
    return exceptions;
  }

  @Override
  public String toString() {
    return "FactoryMethodDescriptor{"
        + "declaration=" + declaration + ", "
        + "name=" + name + ", "
        + "returnType=" + returnType + ", "
        + "publicMethod=" + publicMethod + ", "
        + "overridingMethod=" + overridingMethod + ", "
        + "passedParameters=" + passedParameters + ", "
        + "providedParameters=" + providedParameters + ", "
        + "creationParameters=" + creationParameters + ", "
        + "isVarArgs=" + isVarArgs + ", "
        + "exceptions=" + exceptions
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof FactoryMethodDescriptor) {
      FactoryMethodDescriptor that = (FactoryMethodDescriptor) o;
      return this.declaration.equals(that.declaration())
          && this.name.equals(that.name())
          && this.returnType.equals(that.returnType())
          && this.publicMethod == that.publicMethod()
          && this.overridingMethod == that.overridingMethod()
          && this.passedParameters.equals(that.passedParameters())
          && this.providedParameters.equals(that.providedParameters())
          && this.creationParameters.equals(that.creationParameters())
          && this.isVarArgs == that.isVarArgs()
          && this.exceptions.equals(that.exceptions());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= declaration.hashCode();
    h$ *= 1000003;
    h$ ^= name.hashCode();
    h$ *= 1000003;
    h$ ^= returnType.hashCode();
    h$ *= 1000003;
    h$ ^= publicMethod ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= overridingMethod ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= passedParameters.hashCode();
    h$ *= 1000003;
    h$ ^= providedParameters.hashCode();
    h$ *= 1000003;
    h$ ^= creationParameters.hashCode();
    h$ *= 1000003;
    h$ ^= isVarArgs ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= exceptions.hashCode();
    return h$;
  }

  @Override
  FactoryMethodDescriptor.Builder toBuilder() {
    return new Builder(this);
  }

  static final class Builder extends FactoryMethodDescriptor.Builder {
    private AutoFactoryDeclaration declaration;
    private String name;
    private TypeMirror returnType;
    private Boolean publicMethod;
    private Boolean overridingMethod;
    private ImmutableSet<Parameter> passedParameters;
    private ImmutableSet<Parameter> providedParameters;
    private ImmutableSet<Parameter> creationParameters;
    private Boolean isVarArgs;
    private ImmutableSet<TypeMirror> exceptions;
    Builder() {
    }
    private Builder(FactoryMethodDescriptor source) {
      this.declaration = source.declaration();
      this.name = source.name();
      this.returnType = source.returnType();
      this.publicMethod = source.publicMethod();
      this.overridingMethod = source.overridingMethod();
      this.passedParameters = source.passedParameters();
      this.providedParameters = source.providedParameters();
      this.creationParameters = source.creationParameters();
      this.isVarArgs = source.isVarArgs();
      this.exceptions = source.exceptions();
    }
    @Override
    FactoryMethodDescriptor.Builder declaration(AutoFactoryDeclaration declaration) {
      if (declaration == null) {
        throw new NullPointerException("Null declaration");
      }
      this.declaration = declaration;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder name(String name) {
      if (name == null) {
        throw new NullPointerException("Null name");
      }
      this.name = name;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder returnType(TypeMirror returnType) {
      if (returnType == null) {
        throw new NullPointerException("Null returnType");
      }
      this.returnType = returnType;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder publicMethod(boolean publicMethod) {
      this.publicMethod = publicMethod;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder overridingMethod(boolean overridingMethod) {
      this.overridingMethod = overridingMethod;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder passedParameters(Iterable<Parameter> passedParameters) {
      this.passedParameters = ImmutableSet.copyOf(passedParameters);
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder providedParameters(Iterable<Parameter> providedParameters) {
      this.providedParameters = ImmutableSet.copyOf(providedParameters);
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder creationParameters(Iterable<Parameter> creationParameters) {
      this.creationParameters = ImmutableSet.copyOf(creationParameters);
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder isVarArgs(boolean isVarArgs) {
      this.isVarArgs = isVarArgs;
      return this;
    }
    @Override
    FactoryMethodDescriptor.Builder exceptions(Iterable<? extends TypeMirror> exceptions) {
      this.exceptions = ImmutableSet.copyOf(exceptions);
      return this;
    }
    @Override
    FactoryMethodDescriptor buildImpl() {
      if (this.declaration == null
          || this.name == null
          || this.returnType == null
          || this.publicMethod == null
          || this.overridingMethod == null
          || this.passedParameters == null
          || this.providedParameters == null
          || this.creationParameters == null
          || this.isVarArgs == null
          || this.exceptions == null) {
        StringBuilder missing = new StringBuilder();
        if (this.declaration == null) {
          missing.append(" declaration");
        }
        if (this.name == null) {
          missing.append(" name");
        }
        if (this.returnType == null) {
          missing.append(" returnType");
        }
        if (this.publicMethod == null) {
          missing.append(" publicMethod");
        }
        if (this.overridingMethod == null) {
          missing.append(" overridingMethod");
        }
        if (this.passedParameters == null) {
          missing.append(" passedParameters");
        }
        if (this.providedParameters == null) {
          missing.append(" providedParameters");
        }
        if (this.creationParameters == null) {
          missing.append(" creationParameters");
        }
        if (this.isVarArgs == null) {
          missing.append(" isVarArgs");
        }
        if (this.exceptions == null) {
          missing.append(" exceptions");
        }
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_FactoryMethodDescriptor(
          this.declaration,
          this.name,
          this.returnType,
          this.publicMethod,
          this.overridingMethod,
          this.passedParameters,
          this.providedParameters,
          this.creationParameters,
          this.isVarArgs,
          this.exceptions);
    }
  }

}
