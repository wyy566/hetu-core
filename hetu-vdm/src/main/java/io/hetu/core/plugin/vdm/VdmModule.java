/*
 * Copyright (C) 2018-2020. Huawei Technologies Co., Ltd. All rights reserved.
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
package io.hetu.core.plugin.vdm;

import com.google.inject.Scopes;
import io.airlift.configuration.AbstractConfigurationAwareModule;
import io.prestosql.spi.connector.ConnectorAccessControl;

import static com.google.inject.multibindings.OptionalBinder.newOptionalBinder;
import static io.airlift.configuration.ConfigBinder.configBinder;

/**
 * vdm module
 *
 * @since 2020-02-27
 */
public class VdmModule
        extends AbstractConfigurationAwareModule
{
    @Override
    protected void setup(com.google.inject.Binder binder)
    {
        configBinder(binder).bindConfig(VdmConfig.class);
        newOptionalBinder(binder, ConnectorAccessControl.class);
        binder.bind(VdmConnector.class).in(Scopes.SINGLETON);
    }
}