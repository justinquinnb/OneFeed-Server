# What is OneFeed
*The Free Feed Aggregator*
## About
At its core, OneFeed is an abstraction to the mess that is social media feed APIs. Through this 
abstraction, it offers multi-platform feed aggregation and API request reduction via caching. The 
project accomplishes this with two key components:

1. **The OneFeed API**, which provides a unified interface for retrieving data from social media APIs; and
2. **The OneFeed App**, which uses that, Spring Boot, and a plugin architecture so devs can painlessly configure, serve, and consume their data.

### The API
#### Highlights
1. Unified interface for retrieving social media feed data with ease
2. Mandated support for default content and author implementations by providers to remove the need for DTO handling
3. Preservation of provider-specific DTOs to support mapping data to custom content and author schemas
4. Zero coupling to the OneFeed App, so you're free to use the API however and wherever you please

#### Concept & Goals
The OneFeed API abstracts complex, platform-specific content and author retrieval APIs so you can 
focus on consuming the data, not retrieving it. These methods are simple, reactive, versatile, and 
intuitive. A quick call to `fetchContent(amount, feeds, options, <cursor>)` is the primary 
touchpoint. It outputs a `Flux` of content in the provider's platform's DTO and supports pagination
however the platform handles it (or doesn't).

All `Provider`s (implementations of a social media's content and author APIs in conformance to the 
OneFeed API) are required to provide a `Normalizer` converting the DTOs they output into the 
default `Content` and `Author` implementations the OneFeed API provides.

If you don't want to use the default `Content` and `Author` implementations, you can take the DTOs
as-is and/or map them to a normalized schema of your choice.

Although the OneFeed App heavily relies on the OneFeed API to support its functionalities, the 
OneFeed API is completely independent. So long as your implementations conform to the API, they will 
work for anyone, anywhere, in any context.

### The App
#### Highlights
1. REST API exposing content from any of your configured feeds in a single shape with a single call
2. Hot-swappable plugin support to reduce downtime and promote implementation reuse
3. Content and author data caching to minimize social media API calls and save your wallet
4. Per-feed configurations in a single, centralized file with secret decoupling and siloing
5. Default content and author implementations out-of-the-box, supported by all plugins, with DTOs accessible for caching and serving custom normalizations 

#### Concept & Goals
The OneFeed App, a Spring Boot service, offers a REST API for your clients to fetch an aggregation 
of (theoretically) any social media feed's content and receive normalized, ready-to-display data 
in the shape of your choosing without the headache of juggling platform-specific DTOs and content 
retrieval procedures.

Developers can install plugins from others who've already published their implementations of 
social media APIs in accordance to the OneFeed API. These plugins can be loaded and unloaded at 
runtime.

Similarly, developers can install plugins providing caching services from their favorite database 
providers to significantly reduce platform API usage (and thus costs).

`Provider`s are configurable on a per-feed basis in a single file. Environment variables are 
abstracted with inline references, keeping the service portable. All configurations are handled such 
that plugins only receive the data and secrets *you* designate to them.

Default schemas for `Content` and `Author`s are provided out-of-the-box by the API and supported 
by every `Provider` plugin, meaning you can spin up a functional server with zero code. If you 
prefer your `Content` or `Author`s in a different shape, you can simply fork the project and plumb 
the platform-specific DTOs into a transforming `Normalizer` of your choice.

# Development
## Timeline
Truthfully, as this is a hobbyist project, none at the moment. The goal, however, is a stable 
release by the end of this summer.

## Current Status
This project is currently in early-mid development and thus unstable. At the moment, I'm focusing on 
the following core features:
- Hot-swappable content provider and cache (database) service plugins
  - Developing Instagram and Facebook Provider Plugins alongside the API and app in this repository
    - Helps validate provider plugin loading and unloading on the app side and the interface design of the OneFeed API
    - Provides a live source of data to pull from when testing aggregation endpoints in the app
  - Soon, a Firebase or MongoDB (TBD) "Cacher" (database-based cache) plugin
    - Helps validate cache plugin loading and unloading on the app side and the interface design of the OneFeed API
    - Provides a live cache to pull from when testing aggregation endpoints
- OneFeed App aggregation endpoints
  - Developing basic aggregation endpoints to test the functionality of the system end to end
- Extensibility of Content and Author data types
  - Providing native support for custom Content and Author definitions (rather than just extending an existing schema)
- Painless client-side API implementation
  - Preconfigured aggregations to be referenced by ID with a single, clean OneFeed API hit
  - Choice between minimizing time till first content/author load via stream (reactor-based) endpoints or super speedy implementation with complete response generate

# Use
## Running the App
It runs, but of course is not stable or final in any way. Lots of changes are still in the pipeline 
and lots of core functionality is still pending completion. Give it some time 😅.

## Forking the Project
Probably not a great time to yet!
