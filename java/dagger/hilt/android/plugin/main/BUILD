# Copyright (C) 2020 The Dagger Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Description:
#   A Gradle plugin that performs a transform.

package(default_visibility = ["//:src"])

genrule(
    name = "import-shared-lib",
    srcs = ["//java/dagger/hilt/processor/internal/root/ir"],
    outs = ["processor-shared-lib.jar"],
    cmd = "find $(SRCS)  -type f -name '*.jar' -exec cp {} $@ \\;",
    local = True,
)

filegroup(
    name = "srcs_filegroup",
    srcs = glob(
        ["**/*"],
        # Exclude Gradle build folder to enable working along side Bazel
        exclude = ["**/build/**"],
    ),
)
