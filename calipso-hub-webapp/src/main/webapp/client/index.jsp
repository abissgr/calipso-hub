<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="../includes/head.jsp" %>
</head>
<body class="full-height-column">

    <div class=" full-height-row" id="calipsoHeaderRegion">
    </div>

    <main class="full-height-row-expanded">
        <div class="container configurable-fluid" id="calipsoMainContentRegion"></div>
    </main>

    <div class="full-height-row">
        <div id="calipsoModalRegion" class="modal fade" tabindex='-1'></div>
        <div id="hiddenWrapper" style="display: none"></div>
        <div class="social-form-container">
        </div>
    </div>

    <footer class="full-height-row">
        <div  id="calipsoFooterRegion"></div>
    </footer>

    <!-- Placed at the end of the document so the pages load faster -->
    <script data-main="${basePath}/js/main" id="calipso-script-main" src="${basePath}/js/lib/require.js">
    </script>
</body>
</html>
