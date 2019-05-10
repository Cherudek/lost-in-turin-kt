class LostInTurin : Application() {
    private var refWatcher: RefWatcher? = null

    @Override
    fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        refWatcher = LeakCanary.install(this)
        // Normal app init code...
        // Enable disk persistence only once at the start of the app
        // and before any other Firebase instances
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val favouritesRef = FirebaseDatabase.getInstance()
                .getReference(FIREBASE_ROOT_NODE)
        favouritesRef.keepSynced(true)
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
    }

    companion object {

        private val FIREBASE_ROOT_NODE = "checkouts"

        fun getRefWatcher(context: Context): RefWatcher? {
            val application = context.getApplicationContext() as LostInTurin
            return application.refWatcher
        }
    }
}
