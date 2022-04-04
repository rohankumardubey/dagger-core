/*
 * Copyright (C) 2017 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.internal.codegen.writing;

import static androidx.room.compiler.processing.compat.XConverters.toJavac;
import static com.google.common.collect.Iterables.getOnlyElement;

import androidx.room.compiler.processing.XMethodElement;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import dagger.internal.codegen.binding.ComponentDescriptor.ComponentMethodDescriptor;
import dagger.internal.codegen.binding.MembersInjectionBinding;
import dagger.internal.codegen.javapoet.Expression;

/**
 * A binding expression for members injection component methods. See {@link
 * MembersInjectionMethods}.
 */
final class MembersInjectionRequestRepresentation extends RequestRepresentation {
  private final MembersInjectionBinding binding;
  private final MembersInjectionMethods membersInjectionMethods;

  @AssistedInject
  MembersInjectionRequestRepresentation(
      @Assisted MembersInjectionBinding binding, MembersInjectionMethods membersInjectionMethods) {
    this.binding = binding;
    this.membersInjectionMethods = membersInjectionMethods;
  }

  @Override
  Expression getDependencyExpression(ClassName requestingClass) {
    throw new UnsupportedOperationException(binding.toString());
  }

  @Override
  protected Expression getDependencyExpressionForComponentMethod(
      ComponentMethodDescriptor componentMethod, ComponentImplementation component) {
    XMethodElement methodElement = componentMethod.methodElement();
    ParameterSpec parameter =
        ParameterSpec.get(toJavac(getOnlyElement(methodElement.getParameters())));
    return membersInjectionMethods.getInjectExpression(
        binding.key(), CodeBlock.of("$N", parameter), component.name());
  }

  // TODO(bcorso): Consider making this a method on all RequestRepresentations.
  /** Returns the binding associated with this {@link RequestRepresentation}. */
  MembersInjectionBinding binding() {
    return binding;
  }

  @AssistedFactory
  static interface Factory {
    MembersInjectionRequestRepresentation create(MembersInjectionBinding binding);
  }
}
