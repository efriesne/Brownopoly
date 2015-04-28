<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/html5bp.css">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/board.css">
    <link rel="stylesheet" href="/css/player_panel.css">
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/board_screen_others.css">
    <link rel="stylesheet" href="/css/trades.css">
    <link rel="stylesheet" href="/css/info.css">
    <link rel="stylesheet" href="/css/spectrum.css">
  </head>
  <body>
     ${content}
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="/js/jquery-2.1.1.js"></script>
     <script src="/js/header.js"></script>
     <script src="/js/main.js"></script>
     <script src="/js/home_screen.js"></script>
     <script src="/js/player_panel.js"></script>
     <script src="/js/turn.js"></script>
     <script src="/js/trade.js"></script>
     <script src="/js/button_bar.js"></script>
     <script src="/js/info.js"></script>
     <script src="/js/spectrum.js"></script>
  </body>
  <!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->
</html>
