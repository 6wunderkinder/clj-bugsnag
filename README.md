

# clj-bugsnag

A fully fledged Bugsnag client for Clojure.


## Features

 - Automatically exposes ex-info data as metadata
 - Ring middleware included, attaches ring request map as metadata
 - Include code snippet of the source at crash site
 - Mark project stack traces
 - Middleware and notify function support passing along user-ids to Bugsnag
 - Tested, used in production at [6 Wunderkinder](http://www.6wunderkinder.com/)


## Releases and Dependency Information

clj-bugsnag is released via [Clojars](https://clojars.org/clj-bugsnag). The Latest stable release is 0.1.2

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clojure
[clj-bugsnag "0.1.2"]
```

Maven dependency information:

```xml
<dependency>
  <groupId>clj-bugsnag</groupId>
  <artifactId>clj-bugsnag</artifactId>
  <version>0.1.2</version>
</dependency>
```


## Example Usage

```clojure
(require '[clj-bugsnag.core :as bugsnag]
         '[clj-bugsnag.ring :as bugsnag.ring])

;; Ring middleware:
(bugsnag.ring/wrap-bugsnag handler {:api-key "Project API key"
                                    :environment "production, optional"
                                    :project-ns "your-project-ns-prefix, optional"
                                    :user-from-request (constantly "optional function")})

;; Manual reporting:
(try
  (some-function-that-could-crash some-input)
  (catch Exception exception
    (bugsnag/notify exception
      {:api-key "Project API key"
       ;; Attach custom metadata to create tabs in Bugsnag:
       :meta {:input some-input}})
    
    ;; If no api-key is provided, clj-bugsnag
    ;; will fall back to BUGSNAG_KEY environment variable
    (bugsnag/notify exception)))
```


## License

Copyright © 2014-2015 6 Wunderkinder GmbH.

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.
