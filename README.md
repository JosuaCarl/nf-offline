# nf-offline

## Summary

`nf-offline` is a Nextflow plugin that provides CLI methods to download assets and compiles a config of a nextflow pipeline run,
so it can be run offline.

## Get Started
Run the following command to fetch assets of the da

```bash
nextflow plugin -c nextflow.config nf-offline:download [--plugins|--datasets|--containers] --out <path/to/offline.config>
```

Nextflow downloads the plugin from the Nextflow Registry the first time the pipeline runs.

### Building

To build the plugin:

```bash
make assemble
```

### Testing with Nextflow

The plugin can be tested without a local Nextflow installation:

1. Build and install the plugin to your local Nextflow installation: `make install`
2. Run a pipeline with the plugin: `nextflow run hello -plugins nf-offline@1.0.0-alpha`

### Publishing

Plugins can be published to a central Nextflow registry to make them accessible to the Nextflow community.

Follow these steps to publish the plugin to the Nextflow Registry:

1. Create a file named `$HOME/.gradle/gradle.properties`, where `$HOME` is your home directory. Add the following properties:
    * `npr.apiKey`: Your Nextflow Registry access token.
2. Package your plugin and publish it to the registry: `make release`.

## License

Apache License 2.0. See the [`COPYING`](COPYING) file for details.

Note: The above license is given for guidance only; however the Nextflow Registry
requires the plugin to include an OSS (open source software) license.
