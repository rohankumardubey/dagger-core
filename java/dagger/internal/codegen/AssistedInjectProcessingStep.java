/*
 * Copyright (C) 2021 The Dagger Authors.
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

package dagger.internal.codegen;

import static dagger.internal.codegen.binding.AssistedInjectionAnnotations.assistedInjectAssistedParameters;

import androidx.room.compiler.processing.XConstructorElement;
import androidx.room.compiler.processing.XMessager;
import androidx.room.compiler.processing.XType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import dagger.internal.codegen.binding.AssistedInjectionAnnotations.AssistedParameter;
import dagger.internal.codegen.javapoet.TypeNames;
import dagger.internal.codegen.langmodel.DaggerTypes;
import dagger.internal.codegen.validation.TypeCheckingProcessingStep;
import dagger.internal.codegen.validation.ValidationReport;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/** An annotation processor for {@link dagger.assisted.AssistedInject}-annotated elements. */
final class AssistedInjectProcessingStep extends TypeCheckingProcessingStep<XConstructorElement> {
  private final DaggerTypes types;
  private final XMessager messager;

  @Inject
  AssistedInjectProcessingStep(DaggerTypes types, XMessager messager) {
    this.types = types;
    this.messager = messager;
  }

  @Override
  public ImmutableSet<ClassName> annotationClassNames() {
    return ImmutableSet.of(TypeNames.ASSISTED_INJECT);
  }

  @Override
  protected void process(
      XConstructorElement assistedInjectElement, ImmutableSet<ClassName> annotations) {
    new AssistedInjectValidator().validate(assistedInjectElement).printMessagesTo(messager);
  }

  private final class AssistedInjectValidator {
    ValidationReport validate(XConstructorElement constructor) {
      ValidationReport.Builder report = ValidationReport.about(constructor);

      XType assistedInjectType = constructor.getEnclosingElement().getType();
      ImmutableList<AssistedParameter> assistedParameters =
          assistedInjectAssistedParameters(assistedInjectType, types);

      Set<AssistedParameter> uniqueAssistedParameters = new HashSet<>();
      for (AssistedParameter assistedParameter : assistedParameters) {
        if (!uniqueAssistedParameters.add(assistedParameter)) {
          report.addError(
              String.format(
                  "@AssistedInject constructor has duplicate @Assisted type: %s. Consider setting"
                      + " an identifier on the parameter by using @Assisted(\"identifier\") in both"
                      + " the factory and @AssistedInject constructor",
                  assistedParameter),
              assistedParameter.variableElement());
        }
      }

      return report.build();
    }
  }
}
