# Current End-to-End Aggregation Retrieval Sequence (To Be Revised)
This was/is the original, planned sequence of events that occurs when a user requests an aggregation 
from OneFeed's API. **It is set for revision soon** to reduce latency when Providers must be called, 
likely by responding with cached data immediately and streaming the rest of the data from the 
Providers as it comes in.
```mermaid
sequenceDiagram
    box Your Client
        participant Client
    end
    box OneFeed App
        participant AggregationController
        participant AggregationPackager
        participant AuthorService
        participant ContentService
        participant Cacher
        participant Provider
    end
    box Your External Services
        participant Database Service
        participant Social Media Platform
    end

    Client->>+AggregationController: Request content aggregation
    activate Client
    AggregationController->>+AggregationPackager: Request aggregation package
    activate AggregationController
    par Fetch authors of feeds
        opt Include authors
            AggregationPackager->>+AuthorService: Request authors of feeds
            AuthorService->>+Cacher: Fetch cached authors
            activate AuthorService
                Cacher->>+Database Service: Fetch authors
                activate Cacher
                    Database Service->>-Cacher: Return authors
                deactivate Cacher
                Cacher->>-AuthorService: Return cached authors
            deactivate AuthorService
            opt Cache is missing some authors
                loop For each missing author
                    AuthorService->>+Provider: Request missing author
                    activate AuthorService
                        Provider->>+Social Media Platform: Request author
                        activate Provider
                            Social Media Platform->>-Provider: Respond with author
                        deactivate Provider
                        Provider->>-AuthorService: Return author
                    deactivate AuthorService
                    AuthorService->>+Provider: Get author normalizer
                    activate AuthorService
                        Provider->>-AuthorService: Return author normalizer
                    deactivate AuthorService
                    AuthorService->>AuthorService: Normalize new author
                    AuthorService->>+Cacher: Cache new author
                    Cacher->>-Database Service: Store new author
                end
            end
            AuthorService->>-AggregationPackager: Return desired authors
        end
    and Fetch content from feeds
        AggregationPackager->>+ContentService: Request content from feeds
        ContentService->>+Cacher: Fetch cached content
        activate ContentService
            Cacher->>+Database Service: Fetch content
            activate Cacher
                Database Service->>-Cacher: Return content
            deactivate Cacher
            Cacher->>-ContentService: Return cached content
        deactivate ContentService
        opt Cache is missing some requested content
            loop For each feed without enough cached content
                par
                    ContentService->>+Provider: Request missing amount of content
                    activate ContentService
                        loop For each page necessary to get that amount
                            Provider->>+Social Media Platform: Request page of content
                            activate Provider
                                Social Media Platform->>-Provider: Respond with page of content
                            deactivate Provider
                        end
                        Provider->>-ContentService: Return content
                    deactivate ContentService
                end
            end
            ContentService->+Provider: Get content normalizer
            activate ContentService
                Provider->>-ContentService: Return content normalizer
            deactivate ContentService
            ContentService->>ContentService: Normalize new content
            ContentService->>+Cacher: Cache new content
            Cacher->>-Database Service: Store new content
        end
        ContentService->>ContentService: Sort complete list by timestamp
        ContentService->>-AggregationPackager: Return desired content aggregation
    end
    AggregationPackager->>AggregationPackager: Combine authors with content
    AggregationPackager->>-AggregationController: Return aggregation package
    deactivate AggregationController
    AggregationController->>-Client: Return aggregation package
    deactivate Client
```