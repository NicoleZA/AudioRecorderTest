cordova.define("com.nickza.audiorecorder.AudioRecorder", function(require, exports, module) { module.exports = {
	alert: function(title, message, buttonLabel, successCallback) {
	cordova.exec(successCallback,
				 null, // No failure callback
				 "AudioRecorder",
				 "alert",
				 [title, message, buttonLabel]);
	},
	startRecording: function(filename, successCallback) {
	cordova.exec(successCallback,
				 null, // No failure callback
				 "AudioRecorder",
				 "startRecording",
				 [filename]);
	},
	stopRecording: function(successCallback) {
	cordova.exec(successCallback,
				 null, // No failure callback
				 "AudioRecorder",
				 "stopRecording",
				 []);
	},
	startPlaying: function(filename, successCallback) {
	cordova.exec(successCallback,
				 null, // No failure callback
				 "AudioRecorder",
				 "startPlaying",
				 [filename]);
	},
	stopPlaying: function(successCallback) {
	cordova.exec(successCallback,
				 null, // No failure callback
				 "AudioRecorder",
				 "stopPlaying",
				 []);
	}};
});
