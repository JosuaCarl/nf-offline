/*
 * Copyright 2025, Seqera Labs
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

package qbic.offline

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.plugin.BasePlugin
import nextflow.plugin.Plugins
import org.pf4j.PluginWrapper
import qbic.offline.parsers.ArgsParser

import java.nio.file.Paths

/**
 * The plugin entry point
 */
@CompileStatic
@Slf4j
class OfflinePlugin extends BasePlugin {
    
    String version = readPluginVersion()

    OfflinePlugin(PluginWrapper wrapper) {
        super(wrapper)
    }

    /**
     * Start the plugin. Earliest point of interaction with the core code.
     */
    @Override
    void start() {
        log.info("nf-offline plugin  ~  version ${version}")

        super.start()
    }

    /**
     * Stop the plugin. Latest point of interaction with the core code.
     */
    @Override
    void stop() {
        super.stop()
    }

    static OfflinePlugin getPlugin() {
        if(Plugins.manager) {
            PluginWrapper pluginWrapper = Plugins.manager.getPlugin('nf-co2footprint')
            return pluginWrapper.plugin as OfflinePlugin
        }
        return  null
    }

    /**
     * Set the current plugin version from the local /META-INF/MANIFEST.MF
     *
     * @param manifest URL to the manifest
     * @param tryFallback Whether a fallback in the form of a search of all MANIFESTS from the class loader should be attempted
     */
    static String readPluginVersion(
            URL manifest=OfflinePlugin.class.getResource('/META-INF/MANIFEST.MF'),
            boolean tryFallback=true
    ) {
        try {
            // Get version from manifest
            List<String> lines = manifest.readLines()
            String line = lines.find {String line -> line.startsWith('Plugin-Version: ') }
            return line.split(': ')[1]
        }
        catch (NullPointerException nullPointerException) {
            // Fallback to checking all classLoader Files
            if (tryFallback) {
                URL url = OfflinePlugin.class.protectionDomain.codeSource.location
                url = Paths.get(url.path.replace('/classes/groovy/main', '/tmp/jar/MANIFEST.MF')).toUri().toURL()

                readPluginVersion(url, false)
            }
            else {
                log.error(nullPointerException.getMessage())
                throw nullPointerException
            }
        }
    }

    /**
     * Returns the commands which can be used from within the plugin.
     *
     * @return A list of Strings with command that are implemented in exec()
     */
    @Override
    List<String> getCommands() {
        return OfflineCLI.commands
    }

    /**
     * Function that is executed when `nextflow plugin nf-co2footprint:<CMD> --arg arg1 ...` is called.
     *
     * @param cmd Given command as a String
     * @param args Ordered list of String arguments
     * @return Exit code (0 = OK)
     */
    int exec(String cmd, List<String> args) {
        Map<String, Object> parsedArgs = ArgsParser.parse(args)
        return switch (cmd) {
            case OfflineCLI.downloadCmd -> OfflineCLI.download(parsedArgs)
            default -> {
                System.err.println("Invalid command: ${cmd}")
                1
            }
        }
    }

    /**
     * Function that is executed when `nextflow plugin nf-co2footprint:<CMD> --arg arg1 ...` is called.
     *
     * @param pluginId Should be "nf-co2footprint"
     * @param cmd Given command as a String
     * @param args Ordered list of String arguments
     * @param config A parsed Nextflow config as a Map
     * @return Exit code (0 = OK)
     */
    int exec(String pluginId, String cmd, List<String> args, Map config) {
        Map<String, Object> parsedArgs = ArgsParser.parse(args)
        return switch (cmd) {
            case OfflineCLI.downloadCmd -> OfflineCLI.download(parsedArgs, config)
            default -> {
                System.err.println("Invalid command: ${cmd}")
                1
            }
        }
    }
}

}
