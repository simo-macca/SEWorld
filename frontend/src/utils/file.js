// File extension groups
const CODE_FILES = [
	'c',
	'h',
	'cpp',
	'cc',
	'cxx',
	'hpp',
	'hh',
	'hxx',
	'java',
	'class',
	'jar',
	'js',
	'mjs',
	'cjs',
	'ts',
	'tsx',
	'jsx',
	'py',
	'pyw',
	'pyc',
	'pyo',
	'pyd',
	'go',
	'rs',
	'html',
	'htm',
	'xhtml',
	'css',
	'scss',
	'sass',
	'less',
	'php',
	'phtml',
	'php3',
	'php4',
	'php5',
	'phps',
	'rb',
	'erb',
	'rake',
	'sh',
	'bash',
	'zsh',
	'ksh',
	'lua',
	'swift',
	'kt',
	'kts',
	'dart',
	'sql',
	'r',
	'rmd',
	'm',
	'mat',
	'asm',
	's',
	'S',
	'hs',
	'lhs',
	'scala',
	'sc',
	'pl',
	'pm',
	't',
	'yaml',
	'yml',
	'json',
	'xml',
	'mk',
	'make',
	'mak',
	'cmake',
	'gradle',
	'ninja',
	'clj',
	'cljs',
	'cljc',
	'edn',
	'ex',
	'exs',
	'erl',
	'hrl',
	'tsconfig',
	'ini',
	'toml',
	'conf',
	'cfg',
	'bat',
	'cmd',
	'wasm',
	'wat',
	'md',
	'markdown',
];

const ZIP_FILES = [
	'zip',
	'rar',
	'7z',
	'tar',
	'gz',
	'bz2',
	'xz',
	'lz',
	'lzma',
	'z',
	'iso',
	'jar',
];

const TEXT_FILES = [
	'txt',
	'md',
	'markdown',
	'log',
	'rtf',
	'csv',
	'tsv',
	'json',
	'xml',
	'yaml',
	'yml',
	'ini',
	'cfg',
	'conf',
];

const PRESENTATION_FILES = ['ppt', 'pptx', 'pps', 'ppsx', 'odp', 'key'];

const PDF_FILES = ['pdf'];

const AUDIO_FILES = [
	'mp3',
	'wav',
	'ogg',
	'flac',
	'aac',
	'm4a',
	'wma',
	'aiff',
	'alac',
	'opus',
	'mid',
	'midi',
];

const IMAGE_FILES = [
	'jpg',
	'jpeg',
	'png',
	'gif',
	'bmp',
	'tiff',
	'tif',
	'webp',
	'svg',
	'ico',
	'heic',
	'avif',
	'raw',
	'psd',
	'ai',
	'eps',
];

const VIDEO_FILES = [
	'mp4',
	'mkv',
	'avi',
	'mov',
	'wmv',
	'flv',
	'webm',
	'mpeg',
	'mpg',
	'3gp',
	'm4v',
];

// Enum for types
export const FileType = {
	CODE_FILE: 'CODE_FILE',
	ZIP_FILE: 'ZIP_FILE',
	TEXT_FILE: 'TEXT_FILE',
	PRESENTATION_FILE: 'PRESENTATION_FILE',
	PDF_FILE: 'PDF_FILE',
	AUDIO_FILE: 'AUDIO_FILE',
	IMAGE_FILE: 'IMAGE_FILE',
	VIDEO_FILE: 'VIDEO_FILE',
	UNKNOWN: 'UNKNOWN',
};
export const NOT_A_FILE = 'NOT_A_FILE';

// Checker functions
function hasSuffix(filename, extensions) {
	return extensions.some((ext) => filename.toLowerCase().endsWith(`.${ext}`));
}

function isVideoFile(filename) {
	return hasSuffix(filename, VIDEO_FILES);
}

function isCodeFile(filename) {
	return hasSuffix(filename, CODE_FILES);
}

function isZipFile(filename) {
	return hasSuffix(filename, ZIP_FILES);
}

function isTextFile(filename) {
	return hasSuffix(filename, TEXT_FILES);
}

function isPresentationFile(filename) {
	return hasSuffix(filename, PRESENTATION_FILES);
}

function isPdfFile(filename) {
	return hasSuffix(filename, PDF_FILES);
}

function isAudioFile(filename) {
	return hasSuffix(filename, AUDIO_FILES);
}

function isImageFile(filename) {
	return hasSuffix(filename, IMAGE_FILES);
}

export function detectFileType(filename) {
	if (
		filename === undefined ||
		filename === null ||
		typeof filename !== 'string'
	)
		return NOT_A_FILE;
	if (isCodeFile(filename)) return FileType.CODE_FILE;
	if (isZipFile(filename)) return FileType.ZIP_FILE;
	if (isTextFile(filename)) return FileType.TEXT_FILE;
	if (isPresentationFile(filename)) return FileType.PRESENTATION_FILE;
	if (isPdfFile(filename)) return FileType.PDF_FILE;
	if (isAudioFile(filename)) return FileType.AUDIO_FILE;
	if (isImageFile(filename)) return FileType.IMAGE_FILE;
	if (isVideoFile(filename)) return FileType.VIDEO_FILE;
	return FileType.UNKNOWN;
}
