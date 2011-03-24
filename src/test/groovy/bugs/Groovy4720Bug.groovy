/*
 * Copyright 2003-2011 the original author or authors.
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
package groovy.bugs

/**
 * Groovy-4720: Method overriding with ExpandoMetaClass is partially broken
 *
 * @author Graeme Rocher
 * @author Guillaume Laforge
 */
class Groovy4720Bug extends GroovyTestCase {

    void testBug() {
        def instanceMethods = [DummyApi1.getMethod('test', java.io.Serializable), DummyApi2.getMethod('test', java.io.Serializable)]

        Dummy.metaClass {
            for (method in instanceMethods) {
                def apiInstance = method.getDeclaringClass().newInstance()
                def methodName = method.name
                def parameterTypes = method.parameterTypes
                if (parameterTypes) {
                    parameterTypes = Arrays.copyOfRange(parameterTypes, 0, parameterTypes.length)
                    "$methodName"(new Closure(this) {
                        def call(Object[] args) {
                            apiInstance."$methodName"(* args)
                        }

                        Class[] getParameterTypes() { parameterTypes }
                    })
                }
            }
        }

        assert new Dummy().test(1) == "overrided"
    }
}

class Dummy {}

class DummyApi1 {
    def test(Serializable id) {
        "original"
    }
}
class DummyApi2 {
    def test(Serializable id) {
        "overrided"
    }
}


