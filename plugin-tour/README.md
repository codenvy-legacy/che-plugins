Guided Tour Plugin
==================

![Example](https://raw.githubusercontent.com/benoitf/tour-resources/master/example.png "Example")

The Guided Tour plugin allows to make some on-boarding flows for projects.

## 1. Setup

### Factory setup
Guided Tour is inserted in a factory by using an attribute named 'codenvyGuidedTour'.

```json
{
    "v": "2.0",
    "project": {
        ...
        "attributes": {
            "language": [
                "java"
            ],
            "codenvyGuidedTour": [
                "https://gist.githubusercontent.com/benoitf/.....guidedtour.json"
            ]
        }
    },
    ...
}
```
### For developing a Guided Tour without factory
Add CodenvyGuidedTour.json file in the root folder

## 2. Cheat Sheet

### File content
JSON Codenvy Guided Tour file is composed of root attributes which are
```json
{
"name": "Name of the tour",
"hasWelcomeStep" : boolean,
"steps" : [
    ...All the steps that are in a file
    ]
}
```
#### Root attributes

* 'name' (String): Defines the name of the Guided Tour

* 'hasWelcomeStep' (boolean): if this optional attribute is there, the first step of the Guided Tour will be unnumbered and without any arrows. Following steps will start numbering at 1

* 'steps' (composite): Defines all the steps and their settings that will be displayed

### Step attributes

* title (String) (mandatory): Title of the step (HTML with limited content : only "b", "em", "i", "h1", "h2", "h3", "h4", "h5", "h6", "hr","ul","ol", "li")

* content (String) (mandatory): HTML code that will be displayed as content of the step (HTML with limited content : only "b", "em", "i", "h1","h2", "h3", "h4", "h5", "h6", "hr", "ul", "ol", "li".
  * For inserting image the following syntax is allowed ```![alt name](URL of image) or ![alt name](URL of image = WIDTHxHEIGHT) (HEIGHT being optional)```
  * For inserting links, the wiki hyperlink syntax ``` [http://www.codenvy.com] or [http://www.codenvy.com A title for this link] can be used```

* element (String) (mandatory): HTML dom element that should be checked (example: <em>gwt-debug-MainToolbar/runApp-true</em>)

* nextButtonLabel (String) (optional): Label for the next button. (default is "next")

* skipButton (boolean) (optional): Specify if skip button is displayed (default is true)

* skipButtonLabel (String) (optional): Label for the skip button. (default is "skip")

* placement (String) (mandatory): Where the step is displayed for the element: possible values are LEFT, RIGHT, TOP, BOTTOM

* xOffset (String) (optional): shift the step box from the given offset (could be negative value like "-150")

* yOffset (String) (optional): shift the step box from the given offset (could be negative value like "-150")

* arrowOffset (String) (optional): shift the arrow from the given offset (could be negative value like "-150")

* width  (String) (optional): width of the step box 

* hideArrow (boolean) (optional): hide the arrow of the step

* hideBubbleNumber (boolean) (optional): hide the bubble number of the step

* actions (composite) (optional): actions executed when <em>next</em> button is clicked

* overlays (composite) (optional): overlays that can be displayed behind the step box when the step is displayed on the screen

### actions attributes

  * "trigger &lt;IDE action&gt;" IDE action is for example : <em>runApp</em>

  * "openfile &lt;path to the file&gt;" 

  * "opentab tab-identifier.titleOfTheTab"
   A tab identifier could be NAVIGATION (left of IDE), INFORMATION (bottom of IDE), EDITING (central), TOOLING (right)

  * "openurl &lt;url link&gt;" 

### overlays attributes
* element (String) (optional) If the overlay needs to be positionned from this given element

* url (String) (optional) If provided, the overlay will display the image found on this given URL

* xOffset (String) (optional) offset on X-Axis for the overlay. Without element attribute specified, it will use left border of the screen

* yOffset (String) (optional) offset on Y-Axis for the overlay. Without element attribute specified, it will use top border of the screen

* width (Composite) (optional) Defines width of overlay ```json width": { "value": 100, "unit": "%" }```

* height (Composite) (optional) Defines width of overlay ```json height": { "value": 100, "unit": "%" }```

* zIndex (String) (optional) Defines the Z-Index for the current overlay

* backgroundColor (String) (optional) Defines the background color to set for the given overlay


## 3. Example

Here is an example of CodenvyGuidedTour.json JSON file
```json
{
"name":"GettingStarted",
"hasWelcomeStep": true,
"steps": [
{
"title": "Getting Started with Codenvy",
"content": "Codenvy is the world's most advanced cloud IDE. Code, build, run and share projects from within your browser. Craft your finest code then command non-blocking, non-thrashing Docker microservices with access to unlimited builder and runner resources.\n\nThis guided tour introduces Codenvy’s capabilities. It will take 3 minutes to complete.\n\nIf you need help contact us on our [https://groups.google.com/a/codenvy.com/forum/#!forum/codenvy Google Forum].  This temporary workspace will destroy your work if you become idle.  [https://codenvy.com/site/create-account Sign up for a free account] to save your work.",
"element": "gwt-debug-MainToolbar/runApp-true",
"placement": "BOTTOM",
"xOffset": "200",
"yOffset": "100",
"width": "400",
"skipButtonLabel": "No thanks",
"nextButtonLabel": "Start the tour !",
"overlays": [
{
"zIndex": "5",
"width": {
"value": 100,
"unit": "%"
},
"height": {
"value": 100,
"unit": "%"
},
"backgroundColor": "rgba(50,50,50,0.72)"
},
{
"url": "https://raw.githubusercontent.com/benoitf/tour-resources/master/build.png",
"element": "gwt-debug-MainToolbar/runApp-true",
"xOffset": "-32",
"yOffset": "-2"
},
{
"url": "https://raw.githubusercontent.com/benoitf/tour-resources/master/help.png",
"element": "gwt-debug-MainMenu/helpGroup-true"
},
{
"url": "https://raw.githubusercontent.com/benoitf/tour-resources/master/explorer.png",
"element": "gwt-debug-tabButton-Datasource",
"xOffset": "18",
"yOffset": "-30"
}
]
},
{
"title": "Run the app",
"content": "Let’s start by running the app.  We simplify your work by building the application, downloading dependencies and configuring the app’s runner environment (including RAM and queues) automatically.",
"element": "gwt-debug-MainToolbar/runApp-true",
"placement": "BOTTOM",
"xOffset": "-11",
"yOffset": "-7",
"width": "300",
"actions": [
{
"action": "trigger runApp"
}
]
},
{
"title": "Preview the app",
"content": "Codenvy launches a Docker container for each run. Docker containers are isolated processes with the software to run your code.  Your project’s code and artifacts are copied into the runner automatically.  When the runner has finished loading, the application preview URL will appear.  Clicking the URL will view the application in a new browser tab. Console applications will display output in the console panel.\nClick the URL here to see the application.",
"element": "gwt-debug-Application",
"width": "450",
"placement": "TOP",
"xOffset": "100",
"yOffset": "7"
},
{
"title": "Access the Terminal",
"content": "Every Codenvy runner has a terminal with root access. Try it by typing “top” to get a list of your running processes.",
"element": "gwt-debug-MainToolbar/shutdownApp-true",
"placement": "TOP",
"xOffset": "-40",
"actions": [
{
"action": "openfile src/main/resources/db/hsqldb/initDB.sql"
}
]
},
{
"title": "Edit the app",
"content": "The editor includes key bindings for emacs, vi, and sublime along with syntax highlighting, auto-complete, error warnings, quick-fix, code folding, and international keyboard support.\nTry it by replacing one of the names in the SQL file with your own name.  Use ‘ctrl+space’ to get completion on SQL statements. Save the file when you’re done and re-run the application to see your changes.",
"element": "gwt-debug-projectExplorerTree-panel",
"placement": "LEFT",
"width": "250",
"xOffset": "270",
"yOffset": "70"
},
{
"title": "See your Change",
"content": "Checking your change is as easy as re-running the app.  Now let’s check out the git integration.",
"element": "gwt-debug-MainToolbar/runApp-false",
"placement": "BOTTOM",
"xOffset": "-11",
"yOffset": "-7"
},
{
"title": "Git Integration",
"content": "Codenvy projects may have a local git or subversion repository. The git and svn plugins provide visual access to commands.  From this menu you can add to indexes, commit changes, pull and push - everything you’d want in one place!",
"element": "gwt-debug-MainMenu/git-true",
"placement": "BOTTOM",
"width": "350",
"xOffset": "-5",
"yOffset": "-7"
},
{
"title": "Temporary Workspace",
"content": "This project has been created in a temporary workspace which has 512MB of RAM. Your project will be destroyed if you close the tab or become idle.  We’ll guide you through importing and sign-up.",
"element": "gwt-debug-temporary-workspace-used-toolbar-button",
"placement": "TOP",
"xOffset": "-20"
},
{
"title": "Import your Projects",
"content": "You can import your existing projects quickly and easily. Create a free account to get a named workspace with unlimited private projects, then link your Github credentials (or any of 15 other providers we support).\nWe’ll guide you through the sign-up and import, it’s easy.",
"element": "gwt-debug-MainMenu/git-true",
"placement": "BOTTOM",
"xOffset": "200",
"yOffset": "100",
"width": "600",
"hideArrow": true,
"hideBubbleNumber": true,
"skipButtonLabel": "End tour",
"nextButtonLabel": "Signup",
"actions": [
{
"action": "openurl https://codenvy.com/site/create-account"
}
]
}
]
}
```


