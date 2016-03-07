# Forgotten Fridge




## Creating Action Bar icons

1. Import icon elements from material-design-icons into Inkscape 
ie. https://raw.githubusercontent.com/google/material-design-icons/master/action/svg/production/ic_cached_48px.svg
2. Place them on 96x96 rectangle, export as png (keep transparency)
3. Go to http://romannurik.github.io/AndroidAssetStudio/icons-actionbar.html
4. Image, Don't trim, theme "holo dark" -> download
5. Paste into merge res folders.

## Publish

https://play.google.com/apps/publish/

## Monitor

https://rollbar.com/mirkoczat/Forgotten-Fridge/

## TODO

- DONE: cannot edit exiting items
- DONE: if day < 10, formatting is wrong
- DONE: list of items as List
- DONE: expired items should be RED
- DONE: custom icon for "Synch with cloud"
- DONE: code scanning
- DONE: sync between phones
- DONE: new icon
- DONE: publish
- NEXT VERSION: on sync, you subscribe to GCM
- NEXT VERSION: fab?
- use https instead of http (xiaomi does not like letsencrypt?)