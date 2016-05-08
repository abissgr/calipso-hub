require(['tether-orig'], function(Tether) {
     // attach to global scope untill bootstrap devs fix their module deps
    window.Tether = Tether;
    // it's important to have this, to keep original module definition approach
    return Tether;
});
