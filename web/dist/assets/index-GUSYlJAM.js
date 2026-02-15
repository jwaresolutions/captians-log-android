import{j as t,u as he,a as Y,b as ee,Q as Ja,c as Ya}from"./query-qfgcgxkg.js";import{b as Go,r as b,a as Ke}from"./vendor-ibvuEIEr.js";import{u as de,a as Za,b as Ge,L as oe,c as Xa,R as vn,d as _,B as es}from"./router-CxqMmorT.js";import{d as a,l as z,m as ie,f as ts,o as rs}from"./ui-BNlCdbnp.js";import{l as Ie,T as an,P as _o,M as $e,a as we,b as Qo,W as ns,u as Jo}from"./maps-CDKGOAYI.js";(function(){const r=document.createElement("link").relList;if(r&&r.supports&&r.supports("modulepreload"))return;for(const s of document.querySelectorAll('link[rel="modulepreload"]'))o(s);new MutationObserver(s=>{for(const i of s)if(i.type==="childList")for(const l of i.addedNodes)l.tagName==="LINK"&&l.rel==="modulepreload"&&o(l)}).observe(document,{childList:!0,subtree:!0});function n(s){const i={};return s.integrity&&(i.integrity=s.integrity),s.referrerPolicy&&(i.referrerPolicy=s.referrerPolicy),s.crossOrigin==="use-credentials"?i.credentials="include":s.crossOrigin==="anonymous"?i.credentials="omit":i.credentials="same-origin",i}function o(s){if(s.ep)return;s.ep=!0;const i=n(s);fetch(s.href,i)}})();var sn={},$n=Go;sn.createRoot=$n.createRoot,sn.hydrateRoot=$n.hydrateRoot;function Yo(e,r){return function(){return e.apply(r,arguments)}}const{toString:os}=Object.prototype,{getPrototypeOf:hn}=Object,{iterator:tr,toStringTag:Zo}=Symbol,rr=(e=>r=>{const n=os.call(r);return e[n]||(e[n]=n.slice(8,-1).toLowerCase())})(Object.create(null)),ke=e=>(e=e.toLowerCase(),r=>rr(r)===e),nr=e=>r=>typeof r===e,{isArray:yt}=Array,ft=nr("undefined");function zt(e){return e!==null&&!ft(e)&&e.constructor!==null&&!ft(e.constructor)&&ge(e.constructor.isBuffer)&&e.constructor.isBuffer(e)}const Xo=ke("ArrayBuffer");function as(e){let r;return typeof ArrayBuffer<"u"&&ArrayBuffer.isView?r=ArrayBuffer.isView(e):r=e&&e.buffer&&Xo(e.buffer),r}const ss=nr("string"),ge=nr("function"),ea=nr("number"),Dt=e=>e!==null&&typeof e=="object",is=e=>e===!0||e===!1,Jt=e=>{if(rr(e)!=="object")return!1;const r=hn(e);return(r===null||r===Object.prototype||Object.getPrototypeOf(r)===null)&&!(Zo in e)&&!(tr in e)},ls=e=>{if(!Dt(e)||zt(e))return!1;try{return Object.keys(e).length===0&&Object.getPrototypeOf(e)===Object.prototype}catch{return!1}},cs=ke("Date"),ds=ke("File"),ps=ke("Blob"),ms=ke("FileList"),hs=e=>Dt(e)&&ge(e.pipe),us=e=>{let r;return e&&(typeof FormData=="function"&&e instanceof FormData||ge(e.append)&&((r=rr(e))==="formdata"||r==="object"&&ge(e.toString)&&e.toString()==="[object FormData]"))},gs=ke("URLSearchParams"),[xs,fs,ys,bs]=["ReadableStream","Request","Response","Headers"].map(ke),js=e=>e.trim?e.trim():e.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,"");function It(e,r,{allOwnKeys:n=!1}={}){if(e===null||typeof e>"u")return;let o,s;if(typeof e!="object"&&(e=[e]),yt(e))for(o=0,s=e.length;o<s;o++)r.call(null,e[o],o,e);else{if(zt(e))return;const i=n?Object.getOwnPropertyNames(e):Object.keys(e),l=i.length;let c;for(o=0;o<l;o++)c=i[o],r.call(null,e[c],c,e)}}function ta(e,r){if(zt(e))return null;r=r.toLowerCase();const n=Object.keys(e);let o=n.length,s;for(;o-- >0;)if(s=n[o],r===s.toLowerCase())return s;return null}const rt=typeof globalThis<"u"?globalThis:typeof self<"u"?self:typeof window<"u"?window:global,ra=e=>!ft(e)&&e!==rt;function ln(){const{caseless:e,skipUndefined:r}=ra(this)&&this||{},n={},o=(s,i)=>{const l=e&&ta(n,i)||i;Jt(n[l])&&Jt(s)?n[l]=ln(n[l],s):Jt(s)?n[l]=ln({},s):yt(s)?n[l]=s.slice():(!r||!ft(s))&&(n[l]=s)};for(let s=0,i=arguments.length;s<i;s++)arguments[s]&&It(arguments[s],o);return n}const vs=(e,r,n,{allOwnKeys:o}={})=>(It(r,(s,i)=>{n&&ge(s)?e[i]=Yo(s,n):e[i]=s},{allOwnKeys:o}),e),$s=e=>(e.charCodeAt(0)===65279&&(e=e.slice(1)),e),ws=(e,r,n,o)=>{e.prototype=Object.create(r.prototype,o),e.prototype.constructor=e,Object.defineProperty(e,"super",{value:r.prototype}),n&&Object.assign(e.prototype,n)},Cs=(e,r,n,o)=>{let s,i,l;const c={};if(r=r||{},e==null)return r;do{for(s=Object.getOwnPropertyNames(e),i=s.length;i-- >0;)l=s[i],(!o||o(l,e,r))&&!c[l]&&(r[l]=e[l],c[l]=!0);e=n!==!1&&hn(e)}while(e&&(!n||n(e,r))&&e!==Object.prototype);return r},Ss=(e,r,n)=>{e=String(e),(n===void 0||n>e.length)&&(n=e.length),n-=r.length;const o=e.indexOf(r,n);return o!==-1&&o===n},ks=e=>{if(!e)return null;if(yt(e))return e;let r=e.length;if(!ea(r))return null;const n=new Array(r);for(;r-- >0;)n[r]=e[r];return n},Ts=(e=>r=>e&&r instanceof e)(typeof Uint8Array<"u"&&hn(Uint8Array)),As=(e,r)=>{const o=(e&&e[tr]).call(e);let s;for(;(s=o.next())&&!s.done;){const i=s.value;r.call(e,i[0],i[1])}},Fs=(e,r)=>{let n;const o=[];for(;(n=e.exec(r))!==null;)o.push(n);return o},Es=ke("HTMLFormElement"),Ls=e=>e.toLowerCase().replace(/[-_\s]([a-z\d])(\w*)/g,function(n,o,s){return o.toUpperCase()+s}),wn=(({hasOwnProperty:e})=>(r,n)=>e.call(r,n))(Object.prototype),zs=ke("RegExp"),na=(e,r)=>{const n=Object.getOwnPropertyDescriptors(e),o={};It(n,(s,i)=>{let l;(l=r(s,i,e))!==!1&&(o[i]=l||s)}),Object.defineProperties(e,o)},Ds=e=>{na(e,(r,n)=>{if(ge(e)&&["arguments","caller","callee"].indexOf(n)!==-1)return!1;const o=e[n];if(ge(o)){if(r.enumerable=!1,"writable"in r){r.writable=!1;return}r.set||(r.set=()=>{throw Error("Can not rewrite read-only method '"+n+"'")})}})},Is=(e,r)=>{const n={},o=s=>{s.forEach(i=>{n[i]=!0})};return yt(e)?o(e):o(String(e).split(r)),n},Ms=()=>{},Rs=(e,r)=>e!=null&&Number.isFinite(e=+e)?e:r;function Ns(e){return!!(e&&ge(e.append)&&e[Zo]==="FormData"&&e[tr])}const Ps=e=>{const r=new Array(10),n=(o,s)=>{if(Dt(o)){if(r.indexOf(o)>=0)return;if(zt(o))return o;if(!("toJSON"in o)){r[s]=o;const i=yt(o)?[]:{};return It(o,(l,c)=>{const m=n(l,s+1);!ft(m)&&(i[c]=m)}),r[s]=void 0,i}}return o};return n(e,0)},Bs=ke("AsyncFunction"),Os=e=>e&&(Dt(e)||ge(e))&&ge(e.then)&&ge(e.catch),oa=((e,r)=>e?setImmediate:r?((n,o)=>(rt.addEventListener("message",({source:s,data:i})=>{s===rt&&i===n&&o.length&&o.shift()()},!1),s=>{o.push(s),rt.postMessage(n,"*")}))(`axios@${Math.random()}`,[]):n=>setTimeout(n))(typeof setImmediate=="function",ge(rt.postMessage)),Us=typeof queueMicrotask<"u"?queueMicrotask.bind(rt):typeof process<"u"&&process.nextTick||oa,qs=e=>e!=null&&ge(e[tr]),S={isArray:yt,isArrayBuffer:Xo,isBuffer:zt,isFormData:us,isArrayBufferView:as,isString:ss,isNumber:ea,isBoolean:is,isObject:Dt,isPlainObject:Jt,isEmptyObject:ls,isReadableStream:xs,isRequest:fs,isResponse:ys,isHeaders:bs,isUndefined:ft,isDate:cs,isFile:ds,isBlob:ps,isRegExp:zs,isFunction:ge,isStream:hs,isURLSearchParams:gs,isTypedArray:Ts,isFileList:ms,forEach:It,merge:ln,extend:vs,trim:js,stripBOM:$s,inherits:ws,toFlatObject:Cs,kindOf:rr,kindOfTest:ke,endsWith:Ss,toArray:ks,forEachEntry:As,matchAll:Fs,isHTMLForm:Es,hasOwnProperty:wn,hasOwnProp:wn,reduceDescriptors:na,freezeMethods:Ds,toObjectSet:Is,toCamelCase:Ls,noop:Ms,toFiniteNumber:Rs,findKey:ta,global:rt,isContextDefined:ra,isSpecCompliantForm:Ns,toJSONObject:Ps,isAsyncFn:Bs,isThenable:Os,setImmediate:oa,asap:Us,isIterable:qs};function V(e,r,n,o,s){Error.call(this),Error.captureStackTrace?Error.captureStackTrace(this,this.constructor):this.stack=new Error().stack,this.message=e,this.name="AxiosError",r&&(this.code=r),n&&(this.config=n),o&&(this.request=o),s&&(this.response=s,this.status=s.status?s.status:null)}S.inherits(V,Error,{toJSON:function(){return{message:this.message,name:this.name,description:this.description,number:this.number,fileName:this.fileName,lineNumber:this.lineNumber,columnNumber:this.columnNumber,stack:this.stack,config:S.toJSONObject(this.config),code:this.code,status:this.status}}});const aa=V.prototype,sa={};["ERR_BAD_OPTION_VALUE","ERR_BAD_OPTION","ECONNABORTED","ETIMEDOUT","ERR_NETWORK","ERR_FR_TOO_MANY_REDIRECTS","ERR_DEPRECATED","ERR_BAD_RESPONSE","ERR_BAD_REQUEST","ERR_CANCELED","ERR_NOT_SUPPORT","ERR_INVALID_URL"].forEach(e=>{sa[e]={value:e}});Object.defineProperties(V,sa);Object.defineProperty(aa,"isAxiosError",{value:!0});V.from=(e,r,n,o,s,i)=>{const l=Object.create(aa);S.toFlatObject(e,l,function(h){return h!==Error.prototype},d=>d!=="isAxiosError");const c=e&&e.message?e.message:"Error",m=r==null&&e?e.code:r;return V.call(l,c,m,n,o,s),e&&l.cause==null&&Object.defineProperty(l,"cause",{value:e,configurable:!0}),l.name=e&&e.name||"Error",i&&Object.assign(l,i),l};const Hs=null;function cn(e){return S.isPlainObject(e)||S.isArray(e)}function ia(e){return S.endsWith(e,"[]")?e.slice(0,-2):e}function Cn(e,r,n){return e?e.concat(r).map(function(s,i){return s=ia(s),!n&&i?"["+s+"]":s}).join(n?".":""):r}function Ws(e){return S.isArray(e)&&!e.some(cn)}const Vs=S.toFlatObject(S,{},null,function(r){return/^is[A-Z]/.test(r)});function or(e,r,n){if(!S.isObject(e))throw new TypeError("target must be an object");r=r||new FormData,n=S.toFlatObject(n,{metaTokens:!0,dots:!1,indexes:!1},!1,function(g,u){return!S.isUndefined(u[g])});const o=n.metaTokens,s=n.visitor||h,i=n.dots,l=n.indexes,m=(n.Blob||typeof Blob<"u"&&Blob)&&S.isSpecCompliantForm(r);if(!S.isFunction(s))throw new TypeError("visitor must be a function");function d(p){if(p===null)return"";if(S.isDate(p))return p.toISOString();if(S.isBoolean(p))return p.toString();if(!m&&S.isBlob(p))throw new V("Blob is not supported. Use a Buffer instead.");return S.isArrayBuffer(p)||S.isTypedArray(p)?m&&typeof Blob=="function"?new Blob([p]):Buffer.from(p):p}function h(p,g,u){let x=p;if(p&&!u&&typeof p=="object"){if(S.endsWith(g,"{}"))g=o?g:g.slice(0,-2),p=JSON.stringify(p);else if(S.isArray(p)&&Ws(p)||(S.isFileList(p)||S.endsWith(g,"[]"))&&(x=S.toArray(p)))return g=ia(g),x.forEach(function($,w){!(S.isUndefined($)||$===null)&&r.append(l===!0?Cn([g],w,i):l===null?g:g+"[]",d($))}),!1}return cn(p)?!0:(r.append(Cn(u,g,i),d(p)),!1)}const y=[],j=Object.assign(Vs,{defaultVisitor:h,convertValue:d,isVisitable:cn});function f(p,g){if(!S.isUndefined(p)){if(y.indexOf(p)!==-1)throw Error("Circular reference detected in "+g.join("."));y.push(p),S.forEach(p,function(x,v){(!(S.isUndefined(x)||x===null)&&s.call(r,x,S.isString(v)?v.trim():v,g,j))===!0&&f(x,g?g.concat(v):[v])}),y.pop()}}if(!S.isObject(e))throw new TypeError("data must be an object");return f(e),r}function Sn(e){const r={"!":"%21","'":"%27","(":"%28",")":"%29","~":"%7E","%20":"+","%00":"\0"};return encodeURIComponent(e).replace(/[!'()~]|%20|%00/g,function(o){return r[o]})}function un(e,r){this._pairs=[],e&&or(e,this,r)}const la=un.prototype;la.append=function(r,n){this._pairs.push([r,n])};la.toString=function(r){const n=r?function(o){return r.call(this,o,Sn)}:Sn;return this._pairs.map(function(s){return n(s[0])+"="+n(s[1])},"").join("&")};function Ks(e){return encodeURIComponent(e).replace(/%3A/gi,":").replace(/%24/g,"$").replace(/%2C/gi,",").replace(/%20/g,"+")}function ca(e,r,n){if(!r)return e;const o=n&&n.encode||Ks;S.isFunction(n)&&(n={serialize:n});const s=n&&n.serialize;let i;if(s?i=s(r,n):i=S.isURLSearchParams(r)?r.toString():new un(r,n).toString(o),i){const l=e.indexOf("#");l!==-1&&(e=e.slice(0,l)),e+=(e.indexOf("?")===-1?"?":"&")+i}return e}class kn{constructor(){this.handlers=[]}use(r,n,o){return this.handlers.push({fulfilled:r,rejected:n,synchronous:o?o.synchronous:!1,runWhen:o?o.runWhen:null}),this.handlers.length-1}eject(r){this.handlers[r]&&(this.handlers[r]=null)}clear(){this.handlers&&(this.handlers=[])}forEach(r){S.forEach(this.handlers,function(o){o!==null&&r(o)})}}const da={silentJSONParsing:!0,forcedJSONParsing:!0,clarifyTimeoutError:!1},Gs=typeof URLSearchParams<"u"?URLSearchParams:un,_s=typeof FormData<"u"?FormData:null,Qs=typeof Blob<"u"?Blob:null,Js={isBrowser:!0,classes:{URLSearchParams:Gs,FormData:_s,Blob:Qs},protocols:["http","https","file","blob","url","data"]},gn=typeof window<"u"&&typeof document<"u",dn=typeof navigator=="object"&&navigator||void 0,Ys=gn&&(!dn||["ReactNative","NativeScript","NS"].indexOf(dn.product)<0),Zs=typeof WorkerGlobalScope<"u"&&self instanceof WorkerGlobalScope&&typeof self.importScripts=="function",Xs=gn&&window.location.href||"http://localhost",ei=Object.freeze(Object.defineProperty({__proto__:null,hasBrowserEnv:gn,hasStandardBrowserEnv:Ys,hasStandardBrowserWebWorkerEnv:Zs,navigator:dn,origin:Xs},Symbol.toStringTag,{value:"Module"})),pe={...ei,...Js};function ti(e,r){return or(e,new pe.classes.URLSearchParams,{visitor:function(n,o,s,i){return pe.isNode&&S.isBuffer(n)?(this.append(o,n.toString("base64")),!1):i.defaultVisitor.apply(this,arguments)},...r})}function ri(e){return S.matchAll(/\w+|\[(\w*)]/g,e).map(r=>r[0]==="[]"?"":r[1]||r[0])}function ni(e){const r={},n=Object.keys(e);let o;const s=n.length;let i;for(o=0;o<s;o++)i=n[o],r[i]=e[i];return r}function pa(e){function r(n,o,s,i){let l=n[i++];if(l==="__proto__")return!0;const c=Number.isFinite(+l),m=i>=n.length;return l=!l&&S.isArray(s)?s.length:l,m?(S.hasOwnProp(s,l)?s[l]=[s[l],o]:s[l]=o,!c):((!s[l]||!S.isObject(s[l]))&&(s[l]=[]),r(n,o,s[l],i)&&S.isArray(s[l])&&(s[l]=ni(s[l])),!c)}if(S.isFormData(e)&&S.isFunction(e.entries)){const n={};return S.forEachEntry(e,(o,s)=>{r(ri(o),s,n,0)}),n}return null}function oi(e,r,n){if(S.isString(e))try{return(r||JSON.parse)(e),S.trim(e)}catch(o){if(o.name!=="SyntaxError")throw o}return(n||JSON.stringify)(e)}const Mt={transitional:da,adapter:["xhr","http","fetch"],transformRequest:[function(r,n){const o=n.getContentType()||"",s=o.indexOf("application/json")>-1,i=S.isObject(r);if(i&&S.isHTMLForm(r)&&(r=new FormData(r)),S.isFormData(r))return s?JSON.stringify(pa(r)):r;if(S.isArrayBuffer(r)||S.isBuffer(r)||S.isStream(r)||S.isFile(r)||S.isBlob(r)||S.isReadableStream(r))return r;if(S.isArrayBufferView(r))return r.buffer;if(S.isURLSearchParams(r))return n.setContentType("application/x-www-form-urlencoded;charset=utf-8",!1),r.toString();let c;if(i){if(o.indexOf("application/x-www-form-urlencoded")>-1)return ti(r,this.formSerializer).toString();if((c=S.isFileList(r))||o.indexOf("multipart/form-data")>-1){const m=this.env&&this.env.FormData;return or(c?{"files[]":r}:r,m&&new m,this.formSerializer)}}return i||s?(n.setContentType("application/json",!1),oi(r)):r}],transformResponse:[function(r){const n=this.transitional||Mt.transitional,o=n&&n.forcedJSONParsing,s=this.responseType==="json";if(S.isResponse(r)||S.isReadableStream(r))return r;if(r&&S.isString(r)&&(o&&!this.responseType||s)){const l=!(n&&n.silentJSONParsing)&&s;try{return JSON.parse(r,this.parseReviver)}catch(c){if(l)throw c.name==="SyntaxError"?V.from(c,V.ERR_BAD_RESPONSE,this,null,this.response):c}}return r}],timeout:0,xsrfCookieName:"XSRF-TOKEN",xsrfHeaderName:"X-XSRF-TOKEN",maxContentLength:-1,maxBodyLength:-1,env:{FormData:pe.classes.FormData,Blob:pe.classes.Blob},validateStatus:function(r){return r>=200&&r<300},headers:{common:{Accept:"application/json, text/plain, */*","Content-Type":void 0}}};S.forEach(["delete","get","head","post","put","patch"],e=>{Mt.headers[e]={}});const ai=S.toObjectSet(["age","authorization","content-length","content-type","etag","expires","from","host","if-modified-since","if-unmodified-since","last-modified","location","max-forwards","proxy-authorization","referer","retry-after","user-agent"]),si=e=>{const r={};let n,o,s;return e&&e.split(`
`).forEach(function(l){s=l.indexOf(":"),n=l.substring(0,s).trim().toLowerCase(),o=l.substring(s+1).trim(),!(!n||r[n]&&ai[n])&&(n==="set-cookie"?r[n]?r[n].push(o):r[n]=[o]:r[n]=r[n]?r[n]+", "+o:o)}),r},Tn=Symbol("internals");function jt(e){return e&&String(e).trim().toLowerCase()}function Yt(e){return e===!1||e==null?e:S.isArray(e)?e.map(Yt):String(e)}function ii(e){const r=Object.create(null),n=/([^\s,;=]+)\s*(?:=\s*([^,;]+))?/g;let o;for(;o=n.exec(e);)r[o[1]]=o[2];return r}const li=e=>/^[-_a-zA-Z0-9^`|~,!#$%&'*+.]+$/.test(e.trim());function pr(e,r,n,o,s){if(S.isFunction(o))return o.call(this,r,n);if(s&&(r=n),!!S.isString(r)){if(S.isString(o))return r.indexOf(o)!==-1;if(S.isRegExp(o))return o.test(r)}}function ci(e){return e.trim().toLowerCase().replace(/([a-z\d])(\w*)/g,(r,n,o)=>n.toUpperCase()+o)}function di(e,r){const n=S.toCamelCase(" "+r);["get","set","has"].forEach(o=>{Object.defineProperty(e,o+n,{value:function(s,i,l){return this[o].call(this,r,s,i,l)},configurable:!0})})}let xe=class{constructor(r){r&&this.set(r)}set(r,n,o){const s=this;function i(c,m,d){const h=jt(m);if(!h)throw new Error("header name must be a non-empty string");const y=S.findKey(s,h);(!y||s[y]===void 0||d===!0||d===void 0&&s[y]!==!1)&&(s[y||m]=Yt(c))}const l=(c,m)=>S.forEach(c,(d,h)=>i(d,h,m));if(S.isPlainObject(r)||r instanceof this.constructor)l(r,n);else if(S.isString(r)&&(r=r.trim())&&!li(r))l(si(r),n);else if(S.isObject(r)&&S.isIterable(r)){let c={},m,d;for(const h of r){if(!S.isArray(h))throw TypeError("Object iterator must return a key-value pair");c[d=h[0]]=(m=c[d])?S.isArray(m)?[...m,h[1]]:[m,h[1]]:h[1]}l(c,n)}else r!=null&&i(n,r,o);return this}get(r,n){if(r=jt(r),r){const o=S.findKey(this,r);if(o){const s=this[o];if(!n)return s;if(n===!0)return ii(s);if(S.isFunction(n))return n.call(this,s,o);if(S.isRegExp(n))return n.exec(s);throw new TypeError("parser must be boolean|regexp|function")}}}has(r,n){if(r=jt(r),r){const o=S.findKey(this,r);return!!(o&&this[o]!==void 0&&(!n||pr(this,this[o],o,n)))}return!1}delete(r,n){const o=this;let s=!1;function i(l){if(l=jt(l),l){const c=S.findKey(o,l);c&&(!n||pr(o,o[c],c,n))&&(delete o[c],s=!0)}}return S.isArray(r)?r.forEach(i):i(r),s}clear(r){const n=Object.keys(this);let o=n.length,s=!1;for(;o--;){const i=n[o];(!r||pr(this,this[i],i,r,!0))&&(delete this[i],s=!0)}return s}normalize(r){const n=this,o={};return S.forEach(this,(s,i)=>{const l=S.findKey(o,i);if(l){n[l]=Yt(s),delete n[i];return}const c=r?ci(i):String(i).trim();c!==i&&delete n[i],n[c]=Yt(s),o[c]=!0}),this}concat(...r){return this.constructor.concat(this,...r)}toJSON(r){const n=Object.create(null);return S.forEach(this,(o,s)=>{o!=null&&o!==!1&&(n[s]=r&&S.isArray(o)?o.join(", "):o)}),n}[Symbol.iterator](){return Object.entries(this.toJSON())[Symbol.iterator]()}toString(){return Object.entries(this.toJSON()).map(([r,n])=>r+": "+n).join(`
`)}getSetCookie(){return this.get("set-cookie")||[]}get[Symbol.toStringTag](){return"AxiosHeaders"}static from(r){return r instanceof this?r:new this(r)}static concat(r,...n){const o=new this(r);return n.forEach(s=>o.set(s)),o}static accessor(r){const o=(this[Tn]=this[Tn]={accessors:{}}).accessors,s=this.prototype;function i(l){const c=jt(l);o[c]||(di(s,l),o[c]=!0)}return S.isArray(r)?r.forEach(i):i(r),this}};xe.accessor(["Content-Type","Content-Length","Accept","Accept-Encoding","User-Agent","Authorization"]);S.reduceDescriptors(xe.prototype,({value:e},r)=>{let n=r[0].toUpperCase()+r.slice(1);return{get:()=>e,set(o){this[n]=o}}});S.freezeMethods(xe);function mr(e,r){const n=this||Mt,o=r||n,s=xe.from(o.headers);let i=o.data;return S.forEach(e,function(c){i=c.call(n,i,s.normalize(),r?r.status:void 0)}),s.normalize(),i}function ma(e){return!!(e&&e.__CANCEL__)}function bt(e,r,n){V.call(this,e??"canceled",V.ERR_CANCELED,r,n),this.name="CanceledError"}S.inherits(bt,V,{__CANCEL__:!0});function ha(e,r,n){const o=n.config.validateStatus;!n.status||!o||o(n.status)?e(n):r(new V("Request failed with status code "+n.status,[V.ERR_BAD_REQUEST,V.ERR_BAD_RESPONSE][Math.floor(n.status/100)-4],n.config,n.request,n))}function pi(e){const r=/^([-+\w]{1,25})(:?\/\/|:)/.exec(e);return r&&r[1]||""}function mi(e,r){e=e||10;const n=new Array(e),o=new Array(e);let s=0,i=0,l;return r=r!==void 0?r:1e3,function(m){const d=Date.now(),h=o[i];l||(l=d),n[s]=m,o[s]=d;let y=i,j=0;for(;y!==s;)j+=n[y++],y=y%e;if(s=(s+1)%e,s===i&&(i=(i+1)%e),d-l<r)return;const f=h&&d-h;return f?Math.round(j*1e3/f):void 0}}function hi(e,r){let n=0,o=1e3/r,s,i;const l=(d,h=Date.now())=>{n=h,s=null,i&&(clearTimeout(i),i=null),e(...d)};return[(...d)=>{const h=Date.now(),y=h-n;y>=o?l(d,h):(s=d,i||(i=setTimeout(()=>{i=null,l(s)},o-y)))},()=>s&&l(s)]}const Xt=(e,r,n=3)=>{let o=0;const s=mi(50,250);return hi(i=>{const l=i.loaded,c=i.lengthComputable?i.total:void 0,m=l-o,d=s(m),h=l<=c;o=l;const y={loaded:l,total:c,progress:c?l/c:void 0,bytes:m,rate:d||void 0,estimated:d&&c&&h?(c-l)/d:void 0,event:i,lengthComputable:c!=null,[r?"download":"upload"]:!0};e(y)},n)},An=(e,r)=>{const n=e!=null;return[o=>r[0]({lengthComputable:n,total:e,loaded:o}),r[1]]},Fn=e=>(...r)=>S.asap(()=>e(...r)),ui=pe.hasStandardBrowserEnv?((e,r)=>n=>(n=new URL(n,pe.origin),e.protocol===n.protocol&&e.host===n.host&&(r||e.port===n.port)))(new URL(pe.origin),pe.navigator&&/(msie|trident)/i.test(pe.navigator.userAgent)):()=>!0,gi=pe.hasStandardBrowserEnv?{write(e,r,n,o,s,i,l){if(typeof document>"u")return;const c=[`${e}=${encodeURIComponent(r)}`];S.isNumber(n)&&c.push(`expires=${new Date(n).toUTCString()}`),S.isString(o)&&c.push(`path=${o}`),S.isString(s)&&c.push(`domain=${s}`),i===!0&&c.push("secure"),S.isString(l)&&c.push(`SameSite=${l}`),document.cookie=c.join("; ")},read(e){if(typeof document>"u")return null;const r=document.cookie.match(new RegExp("(?:^|; )"+e+"=([^;]*)"));return r?decodeURIComponent(r[1]):null},remove(e){this.write(e,"",Date.now()-864e5,"/")}}:{write(){},read(){return null},remove(){}};function xi(e){return/^([a-z][a-z\d+\-.]*:)?\/\//i.test(e)}function fi(e,r){return r?e.replace(/\/?\/$/,"")+"/"+r.replace(/^\/+/,""):e}function ua(e,r,n){let o=!xi(r);return e&&(o||n==!1)?fi(e,r):r}const En=e=>e instanceof xe?{...e}:e;function ot(e,r){r=r||{};const n={};function o(d,h,y,j){return S.isPlainObject(d)&&S.isPlainObject(h)?S.merge.call({caseless:j},d,h):S.isPlainObject(h)?S.merge({},h):S.isArray(h)?h.slice():h}function s(d,h,y,j){if(S.isUndefined(h)){if(!S.isUndefined(d))return o(void 0,d,y,j)}else return o(d,h,y,j)}function i(d,h){if(!S.isUndefined(h))return o(void 0,h)}function l(d,h){if(S.isUndefined(h)){if(!S.isUndefined(d))return o(void 0,d)}else return o(void 0,h)}function c(d,h,y){if(y in r)return o(d,h);if(y in e)return o(void 0,d)}const m={url:i,method:i,data:i,baseURL:l,transformRequest:l,transformResponse:l,paramsSerializer:l,timeout:l,timeoutMessage:l,withCredentials:l,withXSRFToken:l,adapter:l,responseType:l,xsrfCookieName:l,xsrfHeaderName:l,onUploadProgress:l,onDownloadProgress:l,decompress:l,maxContentLength:l,maxBodyLength:l,beforeRedirect:l,transport:l,httpAgent:l,httpsAgent:l,cancelToken:l,socketPath:l,responseEncoding:l,validateStatus:c,headers:(d,h,y)=>s(En(d),En(h),y,!0)};return S.forEach(Object.keys({...e,...r}),function(h){const y=m[h]||s,j=y(e[h],r[h],h);S.isUndefined(j)&&y!==c||(n[h]=j)}),n}const ga=e=>{const r=ot({},e);let{data:n,withXSRFToken:o,xsrfHeaderName:s,xsrfCookieName:i,headers:l,auth:c}=r;if(r.headers=l=xe.from(l),r.url=ca(ua(r.baseURL,r.url,r.allowAbsoluteUrls),e.params,e.paramsSerializer),c&&l.set("Authorization","Basic "+btoa((c.username||"")+":"+(c.password?unescape(encodeURIComponent(c.password)):""))),S.isFormData(n)){if(pe.hasStandardBrowserEnv||pe.hasStandardBrowserWebWorkerEnv)l.setContentType(void 0);else if(S.isFunction(n.getHeaders)){const m=n.getHeaders(),d=["content-type","content-length"];Object.entries(m).forEach(([h,y])=>{d.includes(h.toLowerCase())&&l.set(h,y)})}}if(pe.hasStandardBrowserEnv&&(o&&S.isFunction(o)&&(o=o(r)),o||o!==!1&&ui(r.url))){const m=s&&i&&gi.read(i);m&&l.set(s,m)}return r},yi=typeof XMLHttpRequest<"u",bi=yi&&function(e){return new Promise(function(n,o){const s=ga(e);let i=s.data;const l=xe.from(s.headers).normalize();let{responseType:c,onUploadProgress:m,onDownloadProgress:d}=s,h,y,j,f,p;function g(){f&&f(),p&&p(),s.cancelToken&&s.cancelToken.unsubscribe(h),s.signal&&s.signal.removeEventListener("abort",h)}let u=new XMLHttpRequest;u.open(s.method.toUpperCase(),s.url,!0),u.timeout=s.timeout;function x(){if(!u)return;const $=xe.from("getAllResponseHeaders"in u&&u.getAllResponseHeaders()),A={data:!c||c==="text"||c==="json"?u.responseText:u.response,status:u.status,statusText:u.statusText,headers:$,config:e,request:u};ha(function(I){n(I),g()},function(I){o(I),g()},A),u=null}"onloadend"in u?u.onloadend=x:u.onreadystatechange=function(){!u||u.readyState!==4||u.status===0&&!(u.responseURL&&u.responseURL.indexOf("file:")===0)||setTimeout(x)},u.onabort=function(){u&&(o(new V("Request aborted",V.ECONNABORTED,e,u)),u=null)},u.onerror=function(w){const A=w&&w.message?w.message:"Network Error",T=new V(A,V.ERR_NETWORK,e,u);T.event=w||null,o(T),u=null},u.ontimeout=function(){let w=s.timeout?"timeout of "+s.timeout+"ms exceeded":"timeout exceeded";const A=s.transitional||da;s.timeoutErrorMessage&&(w=s.timeoutErrorMessage),o(new V(w,A.clarifyTimeoutError?V.ETIMEDOUT:V.ECONNABORTED,e,u)),u=null},i===void 0&&l.setContentType(null),"setRequestHeader"in u&&S.forEach(l.toJSON(),function(w,A){u.setRequestHeader(A,w)}),S.isUndefined(s.withCredentials)||(u.withCredentials=!!s.withCredentials),c&&c!=="json"&&(u.responseType=s.responseType),d&&([j,p]=Xt(d,!0),u.addEventListener("progress",j)),m&&u.upload&&([y,f]=Xt(m),u.upload.addEventListener("progress",y),u.upload.addEventListener("loadend",f)),(s.cancelToken||s.signal)&&(h=$=>{u&&(o(!$||$.type?new bt(null,e,u):$),u.abort(),u=null)},s.cancelToken&&s.cancelToken.subscribe(h),s.signal&&(s.signal.aborted?h():s.signal.addEventListener("abort",h)));const v=pi(s.url);if(v&&pe.protocols.indexOf(v)===-1){o(new V("Unsupported protocol "+v+":",V.ERR_BAD_REQUEST,e));return}u.send(i||null)})},ji=(e,r)=>{const{length:n}=e=e?e.filter(Boolean):[];if(r||n){let o=new AbortController,s;const i=function(d){if(!s){s=!0,c();const h=d instanceof Error?d:this.reason;o.abort(h instanceof V?h:new bt(h instanceof Error?h.message:h))}};let l=r&&setTimeout(()=>{l=null,i(new V(`timeout ${r} of ms exceeded`,V.ETIMEDOUT))},r);const c=()=>{e&&(l&&clearTimeout(l),l=null,e.forEach(d=>{d.unsubscribe?d.unsubscribe(i):d.removeEventListener("abort",i)}),e=null)};e.forEach(d=>d.addEventListener("abort",i));const{signal:m}=o;return m.unsubscribe=()=>S.asap(c),m}},vi=function*(e,r){let n=e.byteLength;if(n<r){yield e;return}let o=0,s;for(;o<n;)s=o+r,yield e.slice(o,s),o=s},$i=async function*(e,r){for await(const n of wi(e))yield*vi(n,r)},wi=async function*(e){if(e[Symbol.asyncIterator]){yield*e;return}const r=e.getReader();try{for(;;){const{done:n,value:o}=await r.read();if(n)break;yield o}}finally{await r.cancel()}},Ln=(e,r,n,o)=>{const s=$i(e,r);let i=0,l,c=m=>{l||(l=!0,o&&o(m))};return new ReadableStream({async pull(m){try{const{done:d,value:h}=await s.next();if(d){c(),m.close();return}let y=h.byteLength;if(n){let j=i+=y;n(j)}m.enqueue(new Uint8Array(h))}catch(d){throw c(d),d}},cancel(m){return c(m),s.return()}},{highWaterMark:2})},zn=64*1024,{isFunction:Rt}=S,Ci=(({Request:e,Response:r})=>({Request:e,Response:r}))(S.global),{ReadableStream:Dn,TextEncoder:In}=S.global,Mn=(e,...r)=>{try{return!!e(...r)}catch{return!1}},Si=e=>{e=S.merge.call({skipUndefined:!0},Ci,e);const{fetch:r,Request:n,Response:o}=e,s=r?Rt(r):typeof fetch=="function",i=Rt(n),l=Rt(o);if(!s)return!1;const c=s&&Rt(Dn),m=s&&(typeof In=="function"?(p=>g=>p.encode(g))(new In):async p=>new Uint8Array(await new n(p).arrayBuffer())),d=i&&c&&Mn(()=>{let p=!1;const g=new n(pe.origin,{body:new Dn,method:"POST",get duplex(){return p=!0,"half"}}).headers.has("Content-Type");return p&&!g}),h=l&&c&&Mn(()=>S.isReadableStream(new o("").body)),y={stream:h&&(p=>p.body)};s&&["text","arrayBuffer","blob","formData","stream"].forEach(p=>{!y[p]&&(y[p]=(g,u)=>{let x=g&&g[p];if(x)return x.call(g);throw new V(`Response type '${p}' is not supported`,V.ERR_NOT_SUPPORT,u)})});const j=async p=>{if(p==null)return 0;if(S.isBlob(p))return p.size;if(S.isSpecCompliantForm(p))return(await new n(pe.origin,{method:"POST",body:p}).arrayBuffer()).byteLength;if(S.isArrayBufferView(p)||S.isArrayBuffer(p))return p.byteLength;if(S.isURLSearchParams(p)&&(p=p+""),S.isString(p))return(await m(p)).byteLength},f=async(p,g)=>{const u=S.toFiniteNumber(p.getContentLength());return u??j(g)};return async p=>{let{url:g,method:u,data:x,signal:v,cancelToken:$,timeout:w,onDownloadProgress:A,onUploadProgress:T,responseType:I,headers:D,withCredentials:U="same-origin",fetchOptions:F}=ga(p),R=r||fetch;I=I?(I+"").toLowerCase():"text";let H=ji([v,$&&$.toAbortSignal()],w),G=null;const J=H&&H.unsubscribe&&(()=>{H.unsubscribe()});let B;try{if(T&&d&&u!=="get"&&u!=="head"&&(B=await f(D,x))!==0){let K=new n(g,{method:"POST",body:x,duplex:"half"}),le;if(S.isFormData(x)&&(le=K.headers.get("content-type"))&&D.setContentType(le),K.body){const[ae,Te]=An(B,Xt(Fn(T)));x=Ln(K.body,zn,ae,Te)}}S.isString(U)||(U=U?"include":"omit");const te=i&&"credentials"in n.prototype,be={...F,signal:H,method:u.toUpperCase(),headers:D.normalize().toJSON(),body:x,duplex:"half",credentials:te?U:void 0};G=i&&new n(g,be);let N=await(i?R(G,F):R(g,be));const C=h&&(I==="stream"||I==="response");if(h&&(A||C&&J)){const K={};["status","statusText","headers"].forEach(qe=>{K[qe]=N[qe]});const le=S.toFiniteNumber(N.headers.get("content-length")),[ae,Te]=A&&An(le,Xt(Fn(A),!0))||[];N=new o(Ln(N.body,zn,ae,()=>{Te&&Te(),J&&J()}),K)}I=I||"text";let W=await y[S.findKey(y,I)||"text"](N,p);return!C&&J&&J(),await new Promise((K,le)=>{ha(K,le,{data:W,headers:xe.from(N.headers),status:N.status,statusText:N.statusText,config:p,request:G})})}catch(te){throw J&&J(),te&&te.name==="TypeError"&&/Load failed|fetch/i.test(te.message)?Object.assign(new V("Network Error",V.ERR_NETWORK,p,G),{cause:te.cause||te}):V.from(te,te&&te.code,p,G)}}},ki=new Map,xa=e=>{let r=e&&e.env||{};const{fetch:n,Request:o,Response:s}=r,i=[o,s,n];let l=i.length,c=l,m,d,h=ki;for(;c--;)m=i[c],d=h.get(m),d===void 0&&h.set(m,d=c?new Map:Si(r)),h=d;return d};xa();const xn={http:Hs,xhr:bi,fetch:{get:xa}};S.forEach(xn,(e,r)=>{if(e){try{Object.defineProperty(e,"name",{value:r})}catch{}Object.defineProperty(e,"adapterName",{value:r})}});const Rn=e=>`- ${e}`,Ti=e=>S.isFunction(e)||e===null||e===!1;function Ai(e,r){e=S.isArray(e)?e:[e];const{length:n}=e;let o,s;const i={};for(let l=0;l<n;l++){o=e[l];let c;if(s=o,!Ti(o)&&(s=xn[(c=String(o)).toLowerCase()],s===void 0))throw new V(`Unknown adapter '${c}'`);if(s&&(S.isFunction(s)||(s=s.get(r))))break;i[c||"#"+l]=s}if(!s){const l=Object.entries(i).map(([m,d])=>`adapter ${m} `+(d===!1?"is not supported by the environment":"is not available in the build"));let c=n?l.length>1?`since :
`+l.map(Rn).join(`
`):" "+Rn(l[0]):"as no adapter specified";throw new V("There is no suitable adapter to dispatch the request "+c,"ERR_NOT_SUPPORT")}return s}const fa={getAdapter:Ai,adapters:xn};function hr(e){if(e.cancelToken&&e.cancelToken.throwIfRequested(),e.signal&&e.signal.aborted)throw new bt(null,e)}function Nn(e){return hr(e),e.headers=xe.from(e.headers),e.data=mr.call(e,e.transformRequest),["post","put","patch"].indexOf(e.method)!==-1&&e.headers.setContentType("application/x-www-form-urlencoded",!1),fa.getAdapter(e.adapter||Mt.adapter,e)(e).then(function(o){return hr(e),o.data=mr.call(e,e.transformResponse,o),o.headers=xe.from(o.headers),o},function(o){return ma(o)||(hr(e),o&&o.response&&(o.response.data=mr.call(e,e.transformResponse,o.response),o.response.headers=xe.from(o.response.headers))),Promise.reject(o)})}const ya="1.13.2",ar={};["object","boolean","number","function","string","symbol"].forEach((e,r)=>{ar[e]=function(o){return typeof o===e||"a"+(r<1?"n ":" ")+e}});const Pn={};ar.transitional=function(r,n,o){function s(i,l){return"[Axios v"+ya+"] Transitional option '"+i+"'"+l+(o?". "+o:"")}return(i,l,c)=>{if(r===!1)throw new V(s(l," has been removed"+(n?" in "+n:"")),V.ERR_DEPRECATED);return n&&!Pn[l]&&(Pn[l]=!0,console.warn(s(l," has been deprecated since v"+n+" and will be removed in the near future"))),r?r(i,l,c):!0}};ar.spelling=function(r){return(n,o)=>(console.warn(`${o} is likely a misspelling of ${r}`),!0)};function Fi(e,r,n){if(typeof e!="object")throw new V("options must be an object",V.ERR_BAD_OPTION_VALUE);const o=Object.keys(e);let s=o.length;for(;s-- >0;){const i=o[s],l=r[i];if(l){const c=e[i],m=c===void 0||l(c,i,e);if(m!==!0)throw new V("option "+i+" must be "+m,V.ERR_BAD_OPTION_VALUE);continue}if(n!==!0)throw new V("Unknown option "+i,V.ERR_BAD_OPTION)}}const Zt={assertOptions:Fi,validators:ar},Fe=Zt.validators;let nt=class{constructor(r){this.defaults=r||{},this.interceptors={request:new kn,response:new kn}}async request(r,n){try{return await this._request(r,n)}catch(o){if(o instanceof Error){let s={};Error.captureStackTrace?Error.captureStackTrace(s):s=new Error;const i=s.stack?s.stack.replace(/^.+\n/,""):"";try{o.stack?i&&!String(o.stack).endsWith(i.replace(/^.+\n.+\n/,""))&&(o.stack+=`
`+i):o.stack=i}catch{}}throw o}}_request(r,n){typeof r=="string"?(n=n||{},n.url=r):n=r||{},n=ot(this.defaults,n);const{transitional:o,paramsSerializer:s,headers:i}=n;o!==void 0&&Zt.assertOptions(o,{silentJSONParsing:Fe.transitional(Fe.boolean),forcedJSONParsing:Fe.transitional(Fe.boolean),clarifyTimeoutError:Fe.transitional(Fe.boolean)},!1),s!=null&&(S.isFunction(s)?n.paramsSerializer={serialize:s}:Zt.assertOptions(s,{encode:Fe.function,serialize:Fe.function},!0)),n.allowAbsoluteUrls!==void 0||(this.defaults.allowAbsoluteUrls!==void 0?n.allowAbsoluteUrls=this.defaults.allowAbsoluteUrls:n.allowAbsoluteUrls=!0),Zt.assertOptions(n,{baseUrl:Fe.spelling("baseURL"),withXsrfToken:Fe.spelling("withXSRFToken")},!0),n.method=(n.method||this.defaults.method||"get").toLowerCase();let l=i&&S.merge(i.common,i[n.method]);i&&S.forEach(["delete","get","head","post","put","patch","common"],p=>{delete i[p]}),n.headers=xe.concat(l,i);const c=[];let m=!0;this.interceptors.request.forEach(function(g){typeof g.runWhen=="function"&&g.runWhen(n)===!1||(m=m&&g.synchronous,c.unshift(g.fulfilled,g.rejected))});const d=[];this.interceptors.response.forEach(function(g){d.push(g.fulfilled,g.rejected)});let h,y=0,j;if(!m){const p=[Nn.bind(this),void 0];for(p.unshift(...c),p.push(...d),j=p.length,h=Promise.resolve(n);y<j;)h=h.then(p[y++],p[y++]);return h}j=c.length;let f=n;for(;y<j;){const p=c[y++],g=c[y++];try{f=p(f)}catch(u){g.call(this,u);break}}try{h=Nn.call(this,f)}catch(p){return Promise.reject(p)}for(y=0,j=d.length;y<j;)h=h.then(d[y++],d[y++]);return h}getUri(r){r=ot(this.defaults,r);const n=ua(r.baseURL,r.url,r.allowAbsoluteUrls);return ca(n,r.params,r.paramsSerializer)}};S.forEach(["delete","get","head","options"],function(r){nt.prototype[r]=function(n,o){return this.request(ot(o||{},{method:r,url:n,data:(o||{}).data}))}});S.forEach(["post","put","patch"],function(r){function n(o){return function(i,l,c){return this.request(ot(c||{},{method:r,headers:o?{"Content-Type":"multipart/form-data"}:{},url:i,data:l}))}}nt.prototype[r]=n(),nt.prototype[r+"Form"]=n(!0)});let Ei=class ba{constructor(r){if(typeof r!="function")throw new TypeError("executor must be a function.");let n;this.promise=new Promise(function(i){n=i});const o=this;this.promise.then(s=>{if(!o._listeners)return;let i=o._listeners.length;for(;i-- >0;)o._listeners[i](s);o._listeners=null}),this.promise.then=s=>{let i;const l=new Promise(c=>{o.subscribe(c),i=c}).then(s);return l.cancel=function(){o.unsubscribe(i)},l},r(function(i,l,c){o.reason||(o.reason=new bt(i,l,c),n(o.reason))})}throwIfRequested(){if(this.reason)throw this.reason}subscribe(r){if(this.reason){r(this.reason);return}this._listeners?this._listeners.push(r):this._listeners=[r]}unsubscribe(r){if(!this._listeners)return;const n=this._listeners.indexOf(r);n!==-1&&this._listeners.splice(n,1)}toAbortSignal(){const r=new AbortController,n=o=>{r.abort(o)};return this.subscribe(n),r.signal.unsubscribe=()=>this.unsubscribe(n),r.signal}static source(){let r;return{token:new ba(function(s){r=s}),cancel:r}}};function Li(e){return function(n){return e.apply(null,n)}}function zi(e){return S.isObject(e)&&e.isAxiosError===!0}const pn={Continue:100,SwitchingProtocols:101,Processing:102,EarlyHints:103,Ok:200,Created:201,Accepted:202,NonAuthoritativeInformation:203,NoContent:204,ResetContent:205,PartialContent:206,MultiStatus:207,AlreadyReported:208,ImUsed:226,MultipleChoices:300,MovedPermanently:301,Found:302,SeeOther:303,NotModified:304,UseProxy:305,Unused:306,TemporaryRedirect:307,PermanentRedirect:308,BadRequest:400,Unauthorized:401,PaymentRequired:402,Forbidden:403,NotFound:404,MethodNotAllowed:405,NotAcceptable:406,ProxyAuthenticationRequired:407,RequestTimeout:408,Conflict:409,Gone:410,LengthRequired:411,PreconditionFailed:412,PayloadTooLarge:413,UriTooLong:414,UnsupportedMediaType:415,RangeNotSatisfiable:416,ExpectationFailed:417,ImATeapot:418,MisdirectedRequest:421,UnprocessableEntity:422,Locked:423,FailedDependency:424,TooEarly:425,UpgradeRequired:426,PreconditionRequired:428,TooManyRequests:429,RequestHeaderFieldsTooLarge:431,UnavailableForLegalReasons:451,InternalServerError:500,NotImplemented:501,BadGateway:502,ServiceUnavailable:503,GatewayTimeout:504,HttpVersionNotSupported:505,VariantAlsoNegotiates:506,InsufficientStorage:507,LoopDetected:508,NotExtended:510,NetworkAuthenticationRequired:511,WebServerIsDown:521,ConnectionTimedOut:522,OriginIsUnreachable:523,TimeoutOccurred:524,SslHandshakeFailed:525,InvalidSslCertificate:526};Object.entries(pn).forEach(([e,r])=>{pn[r]=e});function ja(e){const r=new nt(e),n=Yo(nt.prototype.request,r);return S.extend(n,nt.prototype,r,{allOwnKeys:!0}),S.extend(n,r,null,{allOwnKeys:!0}),n.create=function(s){return ja(ot(e,s))},n}const re=ja(Mt);re.Axios=nt;re.CanceledError=bt;re.CancelToken=Ei;re.isCancel=ma;re.VERSION=ya;re.toFormData=or;re.AxiosError=V;re.Cancel=re.CanceledError;re.all=function(r){return Promise.all(r)};re.spread=Li;re.isAxiosError=zi;re.mergeConfig=ot;re.AxiosHeaders=xe;re.formToJSON=e=>pa(S.isHTMLForm(e)?new FormData(e):e);re.getAdapter=fa.getAdapter;re.HttpStatusCode=pn;re.default=re;const{Axios:nf,AxiosError:of,CanceledError:af,isCancel:sf,CancelToken:lf,VERSION:cf,all:df,Cancel:pf,isAxiosError:mf,spread:hf,toFormData:uf,AxiosHeaders:gf,HttpStatusCode:xf,formToJSON:ff,getAdapter:yf,mergeConfig:bf}=re;class Di{client;constructor(){const r="/api/v1";localStorage.removeItem("api_base_url"),this.client=re.create({baseURL:r,timeout:1e4,headers:{"Content-Type":"application/json"}}),this.client.interceptors.request.use(n=>{const o=this.getAuthToken();return o&&(n.headers.Authorization=`Bearer ${o}`),n},n=>Promise.reject(n)),this.client.interceptors.response.use(n=>n,n=>{var s,i,l,c,m,d,h,y;if(!n.response){const j={message:n.code==="ECONNABORTED"?"Request timeout. Please try again.":"Network error. Please check your internet connection.",code:"NETWORK_ERROR",details:{originalError:n.message}};return Promise.reject(j)}const o={message:this.getErrorMessage(n),code:n.response.status.toString(),details:n.response.data};switch(n.response.status){case 401:(i=(s=n.config)==null?void 0:s.url)!=null&&i.includes("/auth/login")||(this.clearAuthToken(),o.message="Your session has expired. Please log in again.");break;case 403:o.message="You don't have permission to perform this action.";break;case 404:o.message="The requested resource was not found.";break;case 409:o.message=((c=(l=n.response.data)==null?void 0:l.error)==null?void 0:c.message)||"A conflict occurred. The resource may have been modified.";break;case 422:o.message=((d=(m=n.response.data)==null?void 0:m.error)==null?void 0:d.message)||"Invalid data provided.";break;case 429:o.message="Too many requests. Please wait a moment and try again.";break;case 500:o.message="Server error. Please try again later.";break;case 503:o.message="Service temporarily unavailable. Please try again later.";break}return console.error("API Error:",{status:n.response.status,message:o.message,url:(h=n.config)==null?void 0:h.url,method:(y=n.config)==null?void 0:y.method}),Promise.reject(o)})}getAuthToken(){return localStorage.getItem("auth_token")}setAuthToken(r){localStorage.setItem("auth_token",r)}clearAuthToken(){localStorage.removeItem("auth_token")}getErrorMessage(r){var n,o,s,i,l;return(s=(o=(n=r.response)==null?void 0:n.data)==null?void 0:o.error)!=null&&s.message?r.response.data.error.message:(l=(i=r.response)==null?void 0:i.data)!=null&&l.message?r.response.data.message:r.message?r.message:"An unexpected error occurred"}async retryRequest(r,n=3,o=1e3){let s;for(let i=1;i<=n;i++)try{return await r()}catch(l){if(s=l,l.code&&l.code.startsWith("4")&&l.code!=="408"&&l.code!=="429")throw l;i<n&&await new Promise(c=>setTimeout(c,o*i))}throw s}updateBaseUrl(r){let n;try{const o=new URL(r).origin,s=window.location.origin;o===s||window.location.hostname==="localhost"&&new URL(r).hostname==="localhost"?n="/api/v1":(n=r.replace(/\/$/,""),n.endsWith("/api/v1")||(n+="/api/v1"))}catch{n=r.replace(/\/$/,""),n.endsWith("/api/v1")||(n+="/api/v1")}this.client.defaults.baseURL=n,console.log("API base URL updated to:",n)}async checkConnectivity(){try{return await this.healthCheck(),!0}catch{return!1}}async get(r,n){return(await this.client.get(r,{params:n})).data.data}async post(r,n){return(await this.client.post(r,n)).data.data}async put(r,n){return(await this.client.put(r,n)).data.data}async patch(r,n){return(await this.client.patch(r,n)).data.data}async delete(r){return(await this.client.delete(r)).data.data}async login(r,n){const s=(await this.client.post("/auth/login",{username:r,password:n})).data;return this.setAuthToken(s.token),s}async logout(){try{await this.post("/auth/logout")}finally{this.clearAuthToken()}}async changePassword(r,n){await this.post("/auth/change-password",{currentPassword:r,newPassword:n}),this.clearAuthToken()}async healthCheck(){return(await re.get("/health")).data}async getBoats(){return this.get("/boats")}async getBoat(r){return this.get(`/boats/${r}`)}async createBoat(r){return this.post("/boats",r)}async updateBoat(r,n){return this.put(`/boats/${r}`,n)}async toggleBoatStatus(r,n){return this.patch(`/boats/${r}/status`,{enabled:n})}async setActiveBoat(r){return this.patch(`/boats/${r}/active`)}async getTrips(r){return this.get("/trips",r)}async getTrip(r){return this.get(`/trips/${r}`)}async createTrip(r){return this.post("/trips",r)}async updateTrip(r,n){return this.put(`/trips/${r}`,n)}async addManualData(r,n){return this.patch(`/trips/${r}/manual-data`,n)}async getLicenseProgress(){return this.get("/captain-log/progress")}async getNotes(r){return this.get("/notes",r)}async getNote(r){return this.get(`/notes/${r}`)}async createNote(r){return this.post("/notes",r)}async updateNote(r,n){return this.put(`/notes/${r}`,n)}async deleteNote(r){return this.delete(`/notes/${r}`)}async getTodoLists(r){return this.get("/todos",r?{boatId:r}:void 0)}async getTodoList(r){return this.get(`/todos/${r}`)}async createTodoList(r){return this.post("/todos",r)}async updateTodoList(r,n){return this.put(`/todos/${r}`,n)}async deleteTodoList(r){return this.delete(`/todos/${r}`)}async addTodoItem(r,n){return this.post(`/todos/${r}/items`,{content:n})}async toggleTodoItem(r){return this.patch(`/todos/items/${r}/complete`)}async updateTodoItem(r,n){return this.put(`/todos/items/${r}`,n)}async deleteTodoItem(r){return this.delete(`/todos/items/${r}`)}async getMaintenanceTemplates(r){return this.get("/maintenance/templates",r?{boatId:r}:void 0)}async getMaintenanceTemplate(r){return this.get(`/maintenance/templates/${r}`)}async createMaintenanceTemplate(r){return this.post("/maintenance/templates",r)}async updateMaintenanceTemplate(r,n){return this.put(`/maintenance/templates/${r}`,n)}async deleteMaintenanceTemplate(r){return this.delete(`/maintenance/templates/${r}`)}async getUpcomingMaintenanceEvents(r){return this.get("/maintenance/events/upcoming",r?{boatId:r}:void 0)}async getCompletedMaintenanceEvents(r){return this.get("/maintenance/events/completed",r?{boatId:r}:void 0)}async getMaintenanceEvent(r){return this.get(`/maintenance/events/${r}`)}async completeMaintenanceEvent(r,n){return this.post(`/maintenance/events/${r}/complete`,n)}async getMarkedLocations(r){return this.get("/locations",r)}async getMarkedLocation(r){return this.get(`/locations/${r}`)}async createMarkedLocation(r){return this.post("/locations",r)}async updateMarkedLocation(r,n){return this.put(`/locations/${r}`,n)}async deleteMarkedLocation(r){return this.delete(`/locations/${r}`)}async getNearbyLocations(r,n,o){return this.get("/locations/nearby",{latitude:r,longitude:n,radiusMeters:o})}async uploadPhoto(r,n,o){const s=new FormData;return s.append("photo",r),s.append("entityType",n),s.append("entityId",o),(await this.client.post("/photos",s,{headers:{"Content-Type":"multipart/form-data"}})).data.data}async getPhotos(r,n){return this.get("/photos",{entityType:r,entityId:n})}async deletePhoto(r){return this.delete(`/photos/${r}`)}async getNotifications(){const r=await this.get("/notifications");return Array.isArray(r)?r:(r==null?void 0:r.notifications)||[]}async markNotificationAsRead(r){return this.patch(`/notifications/${r}/read`)}async createBackup(){return this.post("/backups")}async getBackups(){return this.get("/backups")}async downloadBackup(r){return(await this.client.get(`/backups/${r}/download`,{responseType:"blob"})).data}async getViewerSettings(){return this.get("/settings/viewer")}async updateViewerSettings(r){return this.put("/settings/viewer",r)}}const O=new Di,Ii=a.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10000;
  transform: translateY(${e=>e.$show?"0":"-100%"});
  transition: transform 0.3s ease-in-out;
`,Mi=a.div`
  background: ${e=>e.theme.colors.status.warning};
  color: ${e=>e.theme.colors.background};
  padding: ${e=>e.theme.spacing.sm} ${e=>e.theme.spacing.md};
  text-align: center;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: ${e=>e.theme.spacing.md};
`,Ri=a.button`
  background: transparent;
  border: 1px solid ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.background};
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;

  &:hover {
    background: ${e=>e.theme.colors.background}20;
  }
`,Ni=a.div`
  position: static;
  z-index: auto;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  background: ${e=>e.$isOnline?e.theme.colors.status.success:e.theme.colors.status.error};
  color: white;

  &::before {
    content: '';
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: currentColor;
    animation: ${e=>e.$isOnline?"none":"pulse 2s infinite"};
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
`,Pi=({showConnectionStatus:e=!0})=>{const[r,n]=b.useState(navigator.onLine),[o,s]=b.useState(!1),[i,l]=b.useState(!1);b.useEffect(()=>{const m=()=>{n(!0),s(!1),h()},d=()=>{n(!1),s(!0)},h=async()=>{try{!await O.checkConnectivity()&&navigator.onLine&&(n(!1),s(!0))}catch{navigator.onLine&&(n(!1),s(!0))}};window.addEventListener("online",m),window.addEventListener("offline",d),navigator.onLine?h():s(!0);const y=setInterval(()=>{r||h()},3e4);return()=>{window.removeEventListener("online",m),window.removeEventListener("offline",d),clearInterval(y)}},[r]);const c=async()=>{l(!0);try{await O.checkConnectivity()&&(n(!0),s(!1))}catch{}finally{l(!1)}};return t.jsxs(t.Fragment,{children:[t.jsx(Ii,{$show:o,children:t.jsxs(Mi,{children:[t.jsx("span",{children:"⚠ You are currently offline"}),t.jsx(Ri,{onClick:c,disabled:i,children:i?"Checking...":"Retry"})]})}),e&&t.jsx(Ni,{$isOnline:r,children:r?"Online":"Offline"})]})},Bi={primary:z`
    .panel-header {
      background-color: ${e=>e.theme.colors.primary.neonCarrot};
    }

    .panel-content {
      border-color: ${e=>e.theme.colors.primary.neonCarrot};
    }
  `,secondary:z`
    .panel-header {
      background-color: ${e=>e.theme.colors.primary.lilac};
    }

    .panel-content {
      border-color: ${e=>e.theme.colors.primary.lilac};
    }
  `,accent:z`
    .panel-header {
      background-color: ${e=>e.theme.colors.primary.anakiwa};
    }

    .panel-content {
      border-color: ${e=>e.theme.colors.primary.anakiwa};
    }
  `,info:z`
    .panel-header {
      background-color: ${e=>e.theme.colors.primary.mariner};
    }

    .panel-content {
      border-color: ${e=>e.theme.colors.primary.mariner};
    }
  `},Oi={none:z`
    padding: 0;
  `,sm:z`
    padding: ${e=>e.theme.spacing.sm};
  `,md:z`
    padding: ${e=>e.theme.spacing.md};
  `,lg:z`
    padding: ${e=>e.theme.spacing.lg};
  `},Ui=a.div`
  display: flex;
  flex-direction: column;

  ${e=>Bi[e.variant]}
`,qi=a.div`
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 ${e=>e.theme.spacing.md};
  border-radius: ${e=>e.theme.lcars.buttonRadius};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.inverse};
`,Hi=a.div`
  background-color: ${e=>e.theme.colors.background};
  border: 1px solid;
  border-top: none;
  flex: 1;

  ${e=>Oi[e.padding]}
`,L=({children:e,title:r,variant:n="primary",padding:o="md",className:s})=>t.jsxs(Ui,{variant:n,className:s,children:[r&&t.jsx(qi,{className:"panel-header",children:r}),t.jsx(Hi,{padding:o,className:"panel-content",children:e})]}),Wi={primary:z`
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.tanoi};
      box-shadow: ${e=>e.theme.shadows.glowStrong};
    }
  `,secondary:z`
    background-color: ${e=>e.theme.colors.primary.lilac};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #DDA6DD;
    }

    &:active:not(:disabled) {
      background-color: #EEB3EE;
      box-shadow: 0 0 40px rgba(204, 153, 204, 0.5);
    }
  `,accent:z`
    background-color: ${e=>e.theme.colors.primary.anakiwa};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #AAD6FF;
    }

    &:active:not(:disabled) {
      background-color: #BBE0FF;
      box-shadow: 0 0 40px rgba(153, 204, 255, 0.5);
    }
  `,info:z`
    background-color: ${e=>e.theme.colors.primary.mariner};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #4477DD;
    }

    &:active:not(:disabled) {
      background-color: #5588EE;
      box-shadow: 0 0 40px rgba(51, 102, 204, 0.5);
    }
  `,warning:z`
    background-color: ${e=>e.theme.colors.primary.goldenTanoi};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #FFD677;
    }

    &:active:not(:disabled) {
      background-color: #FFE088;
      box-shadow: 0 0 40px rgba(255, 204, 102, 0.5);
    }
  `,danger:z`
    background-color: ${e=>e.theme.colors.status.error};
    color: ${e=>e.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #FF6666;
    }

    &:active:not(:disabled) {
      background-color: #FF7777;
      box-shadow: 0 0 40px rgba(255, 85, 85, 0.5);
    }
  `,sidebar:z`
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.text.inverse};
    border-radius: 0 9999px 9999px 0;

    &:hover:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.tanoi};
      box-shadow: ${e=>e.theme.shadows.glowStrong};
    }
  `,"cap-left":z`
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.text.inverse};
    border-radius: 9999px 0 0 9999px;

    &:hover:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.tanoi};
      box-shadow: ${e=>e.theme.shadows.glowStrong};
    }
  `,"cap-right":z`
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.text.inverse};
    border-radius: 0 9999px 9999px 0;

    &:hover:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${e=>e.theme.colors.primary.tanoi};
      box-shadow: ${e=>e.theme.shadows.glowStrong};
    }
  `},Vi={sm:z`
    height: 28px;
    padding: 0 ${e=>e.theme.spacing.md};
    font-size: ${e=>e.theme.typography.fontSize.sm};
  `,md:z`
    height: 40px;
    padding: 0 ${e=>e.theme.spacing.lg};
    font-size: ${e=>e.theme.typography.fontSize.md};
  `,lg:z`
    height: 56px;
    padding: 0 ${e=>e.theme.spacing.xl};
    font-size: ${e=>e.theme.typography.fontSize.lg};
  `},Ki=a.button`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  border: none;
  border-radius: ${e=>e.theme.lcars.buttonRadius};
  cursor: pointer;
  transition: all ${e=>e.theme.animation.fast} ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: ${e=>e.theme.spacing.sm};
  white-space: nowrap;
  box-shadow: none;
  position: relative;
  overflow: hidden;

  /* Left-to-right sweep hover effect */
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.25);
    transform: translateX(-100%);
    transition: transform 0.35s ease;
    border-radius: inherit;
  }

  &:hover:not(:disabled)::after {
    transform: translateX(0);
  }

  &:active:not(:disabled)::after {
    background: rgba(255, 255, 255, 0.35);
  }

  ${e=>Wi[e.variant]}
  ${e=>Vi[e.size]}

  &:disabled {
    background-color: ${e=>e.theme.colors.interactive.disabled};
    color: ${e=>e.theme.colors.text.muted};
    cursor: not-allowed;
    box-shadow: none;
  }

  &:focus-visible {
    outline: 2px solid ${e=>e.theme.colors.primary.tanoi};
    outline-offset: 2px;
  }
`,k=({children:e,variant:r="primary",size:n="md",disabled:o=!1,onClick:s,className:i,type:l="button"})=>t.jsx(Ki,{variant:r,size:n,disabled:o,onClick:s,className:i,type:l,children:e}),Gi={1:z`
    font-size: ${e=>e.theme.typography.fontSize.xxxl};
  `,2:z`
    font-size: ${e=>e.theme.typography.fontSize.xxl};
  `,3:z`
    font-size: ${e=>e.theme.typography.fontSize.xl};
  `,4:z`
    font-size: ${e=>e.theme.typography.fontSize.lg};
  `,5:z`
    font-size: ${e=>e.theme.typography.fontSize.md};
  `,6:z`
    font-size: ${e=>e.theme.typography.fontSize.md};
  `},_i={neonCarrot:z`
    color: ${e=>e.theme.colors.primary.neonCarrot};
  `,tanoi:z`
    color: ${e=>e.theme.colors.primary.tanoi};
  `,lilac:z`
    color: ${e=>e.theme.colors.primary.lilac};
  `,anakiwa:z`
    color: ${e=>e.theme.colors.primary.anakiwa};
  `,mariner:z`
    color: ${e=>e.theme.colors.primary.mariner};
  `},Qi={left:z`
    text-align: left;
  `,center:z`
    text-align: center;
  `,right:z`
    text-align: right;
  `},Ji={neonCarrot:"#FF9933",tanoi:"#FFCC99",lilac:"#CC99CC",anakiwa:"#99CCFF"},Yi=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,Zi=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  line-height: ${e=>e.theme.typography.lineHeight.tight};
  margin: 0;

  ${e=>Gi[e.level]}
  ${e=>_i[e.color]}
  ${e=>Qi[e.align]}
`,Xi=a.div`
  width: 100%;
  height: 4px;
  background-color: ${e=>e.color};
  border-radius: 0;
`,q=({children:e,level:r=1,color:n="neonCarrot",align:o="left",withBar:s=!1,barColor:i="neonCarrot",className:l})=>{const c=`h${r}`,m=t.jsx(Zi,{as:c,level:r,color:n,align:o,className:l,children:e});return s?t.jsxs(Yi,{children:[m,t.jsx(Xi,{color:Ji[i]})]}):m},va=ie`
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
`,el=ie`
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
`,tl=a.div`
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  max-width: 400px;
  width: 100%;
  animation: ${e=>e.show?va:el} 0.3s ease-in-out;
  
  @media (max-width: 768px) {
    top: 60px;
    right: 10px;
    left: 10px;
    max-width: none;
  }
`,rl=a.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 80vh;
  overflow-y: auto;
`,nl=a.div`
  padding: 16px;
  border-left: 4px solid ${e=>{switch(e.type){case"maintenance":return e.theme.colors.primary.neonCarrot;case"warning":return e.theme.colors.status.warning;case"error":return e.theme.colors.status.error;default:return e.theme.colors.primary.anakiwa}}};
  background: ${e=>e.isRead?e.theme.colors.surface.dark:e.theme.colors.background};
  opacity: ${e=>e.isRead?.7:1};
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: ${e=>e.theme.colors.surface.medium};
  }
`,ol=a.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
`,al=a.div`
  font-weight: bold;
  color: ${e=>e.theme.colors.text.primary};
  font-size: 14px;
`,sl=a.div`
  font-size: 12px;
  color: ${e=>e.theme.colors.text.light};
  white-space: nowrap;
  margin-left: 8px;
`,il=a.div`
  color: ${e=>e.theme.colors.text.light};
  font-size: 13px;
  line-height: 1.4;
`,ll=a.div`
  display: flex;
  gap: 8px;
  margin-top: 12px;
`,cl=a.div`
  position: absolute;
  top: -8px;
  right: -8px;
  background: ${e=>e.theme.colors.status.error};
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: bold;
  min-width: 20px;
  
  ${e=>e.count>99&&`
    border-radius: 10px;
    padding: 0 6px;
    width: auto;
  `}
`,dl=a.button`
  position: relative;
  background: ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.inverse};
  border: none;
  border-radius: 9999px;
  padding: 0 16px;
  height: 32px;
  cursor: pointer;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 14px;
  font-weight: bold;
  text-transform: uppercase;
  transition: filter 0.2s ease;

  &:hover {
    filter: brightness(1.2);
  }

  ${e=>e.$hasUnread&&`
    filter: brightness(1.3);
  `}
`,pl=a.div`
  text-align: center;
  padding: 32px 16px;
  color: ${e=>e.theme.colors.text.light};
`,ml=({className:e})=>{const[r,n]=b.useState([]),[o,s]=b.useState(!1),[i,l]=b.useState(!1),c=(r||[]).filter(p=>!p.read).length;b.useEffect(()=>{m();const p=setInterval(m,3e4);return()=>clearInterval(p)},[]);const m=async()=>{try{l(!0);const p=await O.getNotifications();n(p)}catch(p){console.error("Failed to load notifications:",p)}finally{l(!1)}},d=()=>{s(!o)},h=async p=>{if(!p.read)try{await O.markNotificationAsRead(p.id),n(g=>g.map(u=>u.id===p.id?{...u,read:!0}:u))}catch(g){console.error("Failed to mark notification as read:",g)}},y=async()=>{const p=(r||[]).filter(g=>!g.read);try{await Promise.all(p.map(g=>O.markNotificationAsRead(g.id))),n(g=>g.map(u=>({...u,read:!0})))}catch(g){console.error("Failed to mark all notifications as read:",g)}},j=p=>{const g=new Date(p),x=new Date().getTime()-g.getTime(),v=Math.floor(x/6e4),$=Math.floor(v/60),w=Math.floor($/24);return v<1?"Just now":v<60?`${v}m ago`:$<24?`${$}h ago`:w<7?`${w}d ago`:g.toLocaleDateString()},f=p=>{switch(p){case"maintenance_due":return"🔧";case"system":return"ℹ️";case"warning":return"⚠️";case"error":return"❌";default:return"📢"}};return t.jsxs("div",{className:e,children:[t.jsxs(dl,{onClick:d,$hasUnread:c>0,children:["Alerts",c>0&&t.jsx(cl,{count:c,children:c>99?"99+":c})]}),o&&t.jsx(tl,{show:o,children:t.jsx(L,{children:t.jsxs("div",{style:{padding:"16px"},children:[t.jsxs("div",{style:{display:"flex",justifyContent:"space-between",alignItems:"center",marginBottom:"16px"},children:[t.jsx(q,{level:3,children:"System Alerts"}),t.jsxs("div",{style:{display:"flex",gap:"8px"},children:[c>0&&t.jsx(k,{size:"sm",variant:"secondary",onClick:y,children:"Mark All Read"}),t.jsx(k,{size:"sm",variant:"secondary",onClick:d,children:"Close"})]})]}),i?t.jsx("div",{style:{textAlign:"center",padding:"20px"},children:"Loading notifications..."}):r.length===0?t.jsxs(pl,{children:[t.jsx("div",{style:{fontSize:"32px",marginBottom:"8px"},children:"📭"}),t.jsx("div",{children:"No notifications"})]}):t.jsx(rl,{children:r.map(p=>t.jsxs(nl,{type:p.type,isRead:p.read,onClick:()=>h(p),children:[t.jsxs(ol,{children:[t.jsxs(al,{children:[f(p.type)," ",p.title]}),t.jsx(sl,{children:j(p.createdAt)})]}),t.jsx(il,{children:p.message}),p.entityType&&p.entityId&&t.jsx(ll,{children:t.jsx(k,{size:"sm",variant:"primary",onClick:()=>{const g=p.entityType==="maintenance"?`/maintenance/events/${p.entityId}`:`/${p.entityType}/${p.entityId}`;window.location.href=g},children:"View Details"})})]},p.id))})]})})})]})};a.div`
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 10001;
  padding: 16px 20px;
  border-radius: 8px;
  color: white;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 400px;
  animation: ${va} 0.3s ease-in-out;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  
  background: ${e=>{switch(e.type){case"success":return"#51cf66";case"error":return"#ff6b6b";case"warning":return"#ffd43b";case"info":return"#339af0";default:return"#339af0"}}};
  
  @media (max-width: 768px) {
    bottom: 10px;
    right: 10px;
    left: 10px;
    max-width: none;
  }
`;a.button`
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 18px;
  padding: 0;
  margin-left: auto;
  
  &:hover {
    opacity: 0.7;
  }
`;const fn="200px",hl="60px",Bn="60px",mn="40px",ul="3px",gl="44px",Re="768px",$a=ie`
  from { opacity: 0; }
  to   { opacity: 1; }
`,xl=a.div`
  min-height: 100vh;
  display: grid;
  background: ${e=>e.theme.colors.background};
  grid-template-columns: ${fn} 1fr;
  grid-template-rows: ${Bn} 1fr ${mn};
  grid-template-areas:
    "sidebar header"
    "sidebar content"
    "sidebar footer";
  gap: 0;
  overflow-x: hidden;
  animation: ${$a} 0.6s ease;

  @media (max-width: ${Re}) {
    grid-template-columns: 1fr;
    grid-template-rows: ${Bn} 1fr ${mn};
    grid-template-areas:
      "header"
      "content"
      "footer";
  }
`,fl=a.aside`
  grid-area: sidebar;
  display: flex;
  flex-direction: column;
  gap: ${ul};
  overflow-y: auto;
  overflow-x: hidden;
  animation: ${$a} 0.4s ease;

  @media (max-width: ${Re}) {
    display: none;
  }
`,yl=a.div`
  width: ${fn};
  height: ${hl};
  background: ${e=>e.theme.colors.primary.tanoi};
  position: relative;
  flex-shrink: 0;
  border-radius: 32px 0 0 0;
`,bl=a.div`
  width: ${fn};
  height: ${mn};
  background: ${e=>e.theme.colors.primary.lilac};
  position: relative;
  flex-shrink: 0;
  border-radius: 0 0 0 32px;
  margin-top: auto;
`,Nt=["tanoi","anakiwa","lilac","goldenTanoi","neonCarrot","mariner","anakiwa","lilac","tanoi","neonCarrot","goldenTanoi","mariner"],jl=a.button`
  width: 100%;
  height: ${gl};
  flex-shrink: 0;
  border: none;
  cursor: pointer;
  background: ${e=>e.$color};
  color: ${e=>e.theme.colors.text.inverse};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  text-align: right;
  padding: 0 18px 0 0;
  border-radius: 0 24px 24px 0;
  position: relative;
  overflow: hidden;
  z-index: 0;

  /* Left-to-right sweep hover effect */
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.25);
    transform: translateX(-100%);
    transition: transform 0.35s ease;
    z-index: 0;
    border-radius: inherit;
  }

  &:hover:not(:disabled)::after {
    transform: translateX(0);
  }

  &:active:not(:disabled)::after {
    background: rgba(255, 255, 255, 0.35);
  }

  ${e=>e.$isActive&&z`
    filter: brightness(1.35);
    box-shadow: 0 0 12px currentColor, inset 0 0 8px rgba(255,255,255,0.15);

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 4px;
      bottom: 4px;
      width: 4px;
      background: #fff;
      border-radius: 0 2px 2px 0;
      z-index: 1;
    }
  `}
`,vl=a.div`
  width: 60%;
  height: 3px;
  background: ${e=>e.$color};
  border-radius: 0 2px 2px 0;
  flex-shrink: 0;
  opacity: 0.6;
`,$l=a.header`
  grid-area: header;
  background: ${e=>e.theme.colors.primary.tanoi};
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 24px 0 16px;
  position: relative;

  @media (max-width: ${Re}) {
    border-radius: 0;
    justify-content: center;
  }

  @media (max-width: 480px) {
    padding: 0 8px;
  }
`,wl=a.h1`
  color: ${e=>e.theme.colors.text.inverse};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.xl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.extraWide};
  margin: 0;
  cursor: pointer;
  user-select: none;
  transition: opacity 0.2s;

  &:hover { opacity: 0.8; }

  @media (max-width: ${Re}) {
    font-size: ${e=>e.theme.typography.fontSize.lg};
    letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  }

  @media (max-width: 480px) {
    font-size: ${e=>e.theme.typography.fontSize.md};
    letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  }
`,Cl=a.img`
  height: 40px;
  width: auto;
  cursor: pointer;
  margin-right: 12px;
  filter: drop-shadow(0 0 6px rgba(255, 153, 51, 0.4));
  transition: filter 0.2s;

  &:hover {
    filter: drop-shadow(0 0 10px rgba(255, 153, 51, 0.7));
  }

  @media (max-width: ${Re}) {
    height: 32px;
  }

  @media (max-width: 480px) {
    display: none;
  }
`,Sl=a.span`
  color: ${e=>e.theme.colors.text.inverse};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  margin-right: auto;
  padding-left: 40px;
  opacity: 0.75;

  @media (max-width: ${Re}) {
    display: none;
  }
`,kl=a.main`
  grid-area: content;
  background: ${e=>e.theme.colors.background};
  overflow-y: auto;
  overflow-x: hidden;
  padding: ${e=>e.theme.spacing.lg};

  @media (max-width: ${Re}) {
    padding: ${e=>e.theme.spacing.md};
  }

  @media (max-width: 480px) {
    padding: ${e=>e.theme.spacing.sm};
  }
`,Tl=a.footer`
  grid-area: footer;
  background: ${e=>e.theme.colors.primary.lilac};
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 24px 0 16px;
  position: relative;

  @media (max-width: ${Re}) {
    border-radius: 0;
    justify-content: center;
  }
`,Al=a.span`
  color: ${e=>e.theme.colors.text.inverse};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  opacity: 0.8;
`,Fl=a.div`
  display: none;

  @media (max-width: ${Re}) {
    display: ${e=>e.$open?"flex":"none"};
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.92);
    z-index: ${e=>e.theme.zIndex.modal};
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 24px;
  }
`,El=a.button`
  width: 80%;
  max-width: 320px;
  height: 48px;
  border: none;
  cursor: pointer;
  background: ${e=>e.$isActive?e.$color:`${e.$color}44`};
  color: ${e=>e.$isActive?e.theme.colors.text.inverse:e.$color};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  border-radius: 0 24px 24px 0;
  transition: background 0.15s, transform 0.1s;

  &:hover {
    filter: brightness(1.2);
    transform: translateX(4px);
  }
`,Ll=a.button`
  position: absolute;
  top: 16px;
  right: 16px;
  background: ${e=>e.theme.colors.primary.neonCarrot};
  color: ${e=>e.theme.colors.text.inverse};
  border: none;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  padding: 10px 20px;
  border-radius: 24px;
  cursor: pointer;
`,zl=a.button`
  display: none;
  @media (max-width: ${Re}) {
    display: block;
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: 2px solid ${e=>e.theme.colors.text.inverse};
    color: ${e=>e.theme.colors.text.inverse};
    font-family: ${e=>e.theme.typography.fontFamily.primary};
    font-size: ${e=>e.theme.typography.fontSize.sm};
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    padding: 6px 12px;
    border-radius: 12px;
    cursor: pointer;
  }

  @media (max-width: 480px) {
    display: block;
    position: absolute;
    left: 4px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 11px;
    padding: 4px 8px;
    border-radius: 8px;
    border: 2px solid currentColor;
    background: none;
    color: inherit;
    cursor: pointer;
    text-transform: uppercase;
    font-weight: bold;
    font-family: inherit;
  }
`,On=[{label:"Dashboard",path:"/dashboard"},{label:"Vessels",path:"/boats"},{label:"Trip Log",path:"/trips"},{label:"Notes",path:"/notes"},{label:"To-Do Lists",path:"/todos"},{label:"Maintenance",path:"/maintenance"},{label:"Navigation",path:"/map"},{label:"Reports",path:"/reports"},{label:"Calendar",path:"/calendar"},{label:"Photos",path:"/photos"},{label:"Docs",path:"/docs"},{label:"Settings",path:"/settings"}];function Dl(){const e=new Date,r=e.getFullYear(),n=new Date(r,0,1).getTime(),o=new Date(r+1,0,1).getTime(),s=(e.getTime()-n)/(o-n);return((r-2323)*1e3+s*1e3).toFixed(1)}const Il=({children:e})=>{const r=de(),n=Za(),[o,s]=b.useState(!1),i=d=>d==="/"?n.pathname==="/":d==="/dashboard"?n.pathname==="/dashboard":n.pathname.startsWith(d),l=d=>{r(d),s(!1)},c=Dl(),m=["#664466","#3366CC","#006699","#CC99CC","#FFCC66"];return t.jsxs(xl,{children:[t.jsxs(fl,{children:[t.jsx(yl,{}),On.map((d,h)=>{const y=Nt[h%Nt.length],f={tanoi:"#FFCC99",goldenTanoi:"#FFCC66",neonCarrot:"#FF9933",lilac:"#CC99CC",anakiwa:"#99CCFF",mariner:"#3366CC",paleCanary:"#FFFF99",eggplant:"#664466",bahamBlue:"#006699"}[y]||"#FFCC99";return t.jsxs(Ke.Fragment,{children:[h>0&&t.jsx(vl,{$color:m[h%m.length]}),t.jsx(jl,{$color:f,$isActive:i(d.path),onClick:()=>l(d.path),"aria-current":i(d.path)?"page":void 0,children:d.label})]},d.path)}),t.jsx(bl,{})]}),t.jsxs($l,{children:[t.jsx(zl,{onClick:()=>s(!0),children:"Menu"}),t.jsxs(Sl,{children:["Stardate ",c," (",new Date().toLocaleDateString("en-US",{month:"short",day:"numeric",year:"numeric"}),")"]}),t.jsx(Cl,{src:"/assets/captains-log-logo.png",alt:"Captain's Log",onClick:()=>l("/")}),t.jsx(wl,{onClick:()=>l("/"),children:"Captain's Log"}),t.jsx("div",{style:{marginLeft:"8px"},children:t.jsx(ml,{})})]}),t.jsx(kl,{children:e}),t.jsxs(Tl,{children:[t.jsx(Pi,{}),t.jsx(Al,{style:{marginLeft:"auto"},children:"LCARS v47.3 — Library Computer Access/Retrieval System"})]}),t.jsxs(Fl,{$open:o,children:[t.jsx(Ll,{onClick:()=>s(!1),children:"Close"}),On.map((d,h)=>{const j={tanoi:"#FFCC99",goldenTanoi:"#FFCC66",neonCarrot:"#FF9933",lilac:"#CC99CC",anakiwa:"#99CCFF",mariner:"#3366CC",paleCanary:"#FFFF99",eggplant:"#664466",bahamBlue:"#006699"}[Nt[h%Nt.length]]||"#FFCC99";return t.jsx(El,{$color:j,$isActive:i(d.path),onClick:()=>l(d.path),children:d.label},d.path)})]})]})},Ml=a.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: ${e=>e.theme.spacing.xl};
  text-align: center;
`,Rl=a.div`
  color: ${e=>e.theme.colors.status.error};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  margin: ${e=>e.theme.spacing.lg} 0;
`;a.details`
  margin-top: ${e=>e.theme.spacing.lg};
  padding: ${e=>e.theme.spacing.md};
  background: ${e=>e.theme.colors.surface.dark};
  border-radius: 4px;
  border: 1px solid ${e=>e.theme.colors.status.error};
  max-width: 600px;
  
  summary {
    cursor: pointer;
    color: ${e=>e.theme.colors.status.error};
    font-weight: bold;
    margin-bottom: ${e=>e.theme.spacing.sm};
  }
  
  pre {
    font-size: ${e=>e.theme.typography.fontSize.sm};
    color: ${e=>e.theme.colors.text.secondary};
    white-space: pre-wrap;
    word-break: break-word;
    margin: 0;
  }
`;const Nl=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  margin-top: ${e=>e.theme.spacing.xl};
`;class Pl extends b.Component{constructor(r){super(r),this.state={hasError:!1}}static getDerivedStateFromError(r){return{hasError:!0,error:r}}componentDidCatch(r,n){console.error("Error caught by boundary:",r,n),this.setState({error:r,errorInfo:n}),console.error("Production error:",{error:r.message,stack:r.stack,componentStack:n.componentStack})}handleReload=()=>{window.location.reload()};handleGoHome=()=>{window.location.href="/"};handleRetry=()=>{this.setState({hasError:!1,error:void 0,errorInfo:void 0})};render(){return this.state.hasError?this.props.fallback?this.props.fallback:t.jsx(Ml,{children:t.jsxs(L,{children:[t.jsx(q,{level:1,children:"System Error"}),t.jsx(Rl,{children:"An unexpected error has occurred in the application."}),t.jsx("p",{children:"The error has been logged and will be investigated. You can try reloading the page or returning to the dashboard."}),t.jsxs(Nl,{children:[t.jsx(k,{onClick:this.handleRetry,variant:"primary",children:"Try Again"}),t.jsx(k,{onClick:this.handleReload,variant:"secondary",children:"Reload Page"}),t.jsx(k,{onClick:this.handleGoHome,variant:"secondary",children:"Go to Dashboard"})]}),!1]})}):this.props.children}}const Bl={neonCarrot:z`
    background-color: ${e=>e.theme.colors.primary.neonCarrot};

    &::before {
      background-color: ${e=>e.theme.colors.background};
    }
  `,tanoi:z`
    background-color: ${e=>e.theme.colors.primary.tanoi};

    &::before {
      background-color: ${e=>e.theme.colors.background};
    }
  `,lilac:z`
    background-color: ${e=>e.theme.colors.primary.lilac};

    &::before {
      background-color: ${e=>e.theme.colors.background};
    }
  `,anakiwa:z`
    background-color: ${e=>e.theme.colors.primary.anakiwa};

    &::before {
      background-color: ${e=>e.theme.colors.background};
    }
  `,mariner:z`
    background-color: ${e=>e.theme.colors.primary.mariner};

    &::before {
      background-color: ${e=>e.theme.colors.background};
    }
  `};a.div`
  position: relative;
  width: ${e=>e.size}px;
  height: ${e=>e.size}px;
  flex-shrink: 0;

  ${e=>Bl[e.color]}

  /* Create the quarter-circle cutout using a pseudo-element */
  &::before {
    content: '';
    position: absolute;
    width: ${e=>e.size-e.armWidth}px;
    height: ${e=>e.size-e.armWidth}px;
  }

  /* Position the cutout based on elbow orientation */
  ${e=>{switch(e.position){case"top-left":return z`
          &::before {
            bottom: 0;
            right: 0;
            border-radius: 0 0 0 ${e.size-e.armWidth}px;
          }
        `;case"top-right":return z`
          &::before {
            bottom: 0;
            left: 0;
            border-radius: 0 0 ${e.size-e.armWidth}px 0;
          }
        `;case"bottom-left":return z`
          &::before {
            top: 0;
            right: 0;
            border-radius: 0 ${e.size-e.armWidth}px 0 0;
          }
        `;case"bottom-right":return z`
          &::before {
            top: 0;
            left: 0;
            border-radius: ${e.size-e.armWidth}px 0 0 0;
          }
        `}}}
`;a.div`
  display: flex;
  flex-direction: ${e=>e.orientation==="horizontal"?"row":"column"};
  flex-shrink: 0;
  width: ${e=>typeof e.width=="number"?`${e.width}px`:e.width};
  height: ${e=>typeof e.height=="number"?`${e.height}px`:e.height};
  gap: ${e=>e.isSegmented?e.theme.lcars.gap:"0"};
  border-radius: 0;
  overflow: hidden;
`;a.div`
  background-color: ${e=>e.color};
  flex: ${e=>e.flex||1};
  border-radius: 0;
`;const Ol=a.div`
  display: flex;
  flex-direction: column;
  width: ${e=>typeof e.width=="number"?`${e.width}px`:e.width};
  gap: ${e=>e.gap};
  min-height: 100%;

  > * {
    width: 100%;
    flex-shrink: 0;
  }
`,Me=({children:e,width:r="200px",gap:n="3px",className:o})=>t.jsx(Ol,{width:r,gap:n,className:o,children:e}),Ul={sm:z`
    .data-label {
      font-size: ${e=>e.theme.typography.fontSize.xs};
    }
    .data-value {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
    .data-unit {
      font-size: ${e=>e.theme.typography.fontSize.sm};
    }
  `,md:z`
    .data-label {
      font-size: ${e=>e.theme.typography.fontSize.sm};
    }
    .data-value {
      font-size: ${e=>e.theme.typography.fontSize.lg};
    }
    .data-unit {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
  `,lg:z`
    .data-label {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
    .data-value {
      font-size: ${e=>e.theme.typography.fontSize.xl};
    }
    .data-unit {
      font-size: ${e=>e.theme.typography.fontSize.lg};
    }
  `},ql={neonCarrot:z`
    color: ${e=>e.theme.colors.primary.neonCarrot};
  `,lilac:z`
    color: ${e=>e.theme.colors.primary.lilac};
  `,anakiwa:z`
    color: ${e=>e.theme.colors.primary.anakiwa};
  `,mariner:z`
    color: ${e=>e.theme.colors.primary.mariner};
  `,success:z`
    color: ${e=>e.theme.colors.status.success};
  `},Hl={neonCarrot:"#FF9933",lilac:"#CC99CC",anakiwa:"#99CCFF",success:"#55FF55",error:"#FF5555"},Wl=a.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: ${e=>e.theme.spacing.xs};
  background-color: transparent;

  ${e=>Ul[e.size]}
`,Vl=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.normal};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  color: ${e=>e.theme.colors.primary.lilac};
  opacity: 0.8;
`,Kl=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.sm};
`,Gl=a.div`
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: ${e=>e.color};
  box-shadow: 0 0 8px ${e=>e.color};
  flex-shrink: 0;
`,_l=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  line-height: ${e=>e.theme.typography.lineHeight.tight};

  ${e=>ql[e.valueColor]}
`,Ql=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.normal};
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
`,E=({label:e,value:r,unit:n,size:o="md",valueColor:s="neonCarrot",showIndicator:i=!1,indicatorColor:l="neonCarrot",className:c})=>t.jsxs(Wl,{size:o,className:c,children:[t.jsx(Vl,{className:"data-label",children:e}),t.jsxs(Kl,{children:[i&&t.jsx(Gl,{color:Hl[l]}),t.jsx(_l,{className:"data-value",valueColor:s,children:r}),n&&t.jsx(Ql,{className:"data-unit",children:n})]})]}),Jl={info:z`
    background-color: ${e=>e.theme.colors.primary.anakiwa};
    border-color: #AAD6FF;
    color: ${e=>e.theme.colors.text.inverse};
  `,success:z`
    background-color: ${e=>e.theme.colors.status.success};
    border-color: #88FF88;
    color: ${e=>e.theme.colors.text.inverse};
  `,warning:z`
    background-color: ${e=>e.theme.colors.status.warning};
    border-color: #FFFF88;
    color: ${e=>e.theme.colors.text.inverse};
  `,error:z`
    background-color: ${e=>e.theme.colors.status.error};
    border-color: #FF8888;
    color: ${e=>e.theme.colors.text.inverse};
  `},Yl=a.div.withConfig({shouldForwardProp:e=>!["type","blink"].includes(e)})`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: ${e=>e.theme.spacing.md};
  border: 2px solid;
  border-radius: ${e=>e.theme.borderRadius.lg};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;

  ${e=>Jl[e.type]}

  ${e=>e.blink&&z`
    animation: lcars-blink 1s infinite;
  `}
`,Zl=a.div`
  flex: 1;
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.sm};
`,Xl=a.div`
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,ec=a.div`
  flex: 1;
`,tc=a.button`
  background: none;
  border: none;
  color: inherit;
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  cursor: pointer;
  padding: ${e=>e.theme.spacing.xs};
  border-radius: ${e=>e.theme.borderRadius.sm};
  transition: background-color ${e=>e.theme.animation.fast} ease;

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  &:focus {
    outline: 2px solid rgba(255, 255, 255, 0.5);
    outline-offset: 2px;
  }
`,rc=e=>{switch(e){case"info":return"ℹ";case"success":return"✓";case"warning":return"⚠";case"error":return"✗";default:return"ℹ"}},Se=({children:e,type:r="info",blink:n=!1,dismissible:o=!1,onDismiss:s,className:i})=>t.jsxs(Yl,{type:r,blink:n,className:i,children:[t.jsxs(Zl,{children:[t.jsx(Xl,{children:rc(r)}),t.jsx(ec,{children:e})]}),o&&s&&t.jsx(tc,{onClick:s,"aria-label":"Dismiss alert",children:"×"})]}),nc={neonCarrot:z`
    .progress-fill {
      background: linear-gradient(90deg,
        ${e=>e.theme.colors.primary.neonCarrot} 0%,
        ${e=>e.theme.colors.primary.goldenTanoi} 100%
      );
    }
    .progress-text {
      color: ${e=>e.theme.colors.primary.neonCarrot};
    }
  `,lilac:z`
    .progress-fill {
      background: linear-gradient(90deg,
        ${e=>e.theme.colors.primary.lilac} 0%,
        #DDA6DD 100%
      );
    }
    .progress-text {
      color: ${e=>e.theme.colors.primary.lilac};
    }
  `,anakiwa:z`
    .progress-fill {
      background: linear-gradient(90deg,
        ${e=>e.theme.colors.primary.anakiwa} 0%,
        #AAD6FF 100%
      );
    }
    .progress-text {
      color: ${e=>e.theme.colors.primary.anakiwa};
    }
  `,success:z`
    .progress-fill {
      background: linear-gradient(90deg,
        ${e=>e.theme.colors.status.success} 0%,
        #88FF88 100%
      );
    }
    .progress-text {
      color: ${e=>e.theme.colors.status.success};
    }
  `},oc={sm:z`
    .chart-title {
      font-size: ${e=>e.theme.typography.fontSize.sm};
      margin-bottom: ${e=>e.theme.spacing.sm};
    }
    .progress-bar {
      height: 12px;
    }
    .progress-stats {
      font-size: ${e=>e.theme.typography.fontSize.xs};
      margin-top: ${e=>e.theme.spacing.sm};
    }
  `,md:z`
    .chart-title {
      font-size: ${e=>e.theme.typography.fontSize.md};
      margin-bottom: ${e=>e.theme.spacing.md};
    }
    .progress-bar {
      height: 16px;
    }
    .progress-stats {
      font-size: ${e=>e.theme.typography.fontSize.sm};
      margin-top: ${e=>e.theme.spacing.md};
    }
  `,lg:z`
    .chart-title {
      font-size: ${e=>e.theme.typography.fontSize.lg};
      margin-bottom: ${e=>e.theme.spacing.lg};
    }
    .progress-bar {
      height: 20px;
    }
    .progress-stats {
      font-size: ${e=>e.theme.typography.fontSize.md};
      margin-top: ${e=>e.theme.spacing.lg};
    }
  `},ac=a.div`
  ${e=>nc[e.color]}
  ${e=>oc[e.size]}
`,sc=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  color: ${e=>e.theme.colors.text.primary};
`,ic=a.div`
  background-color: ${e=>e.theme.colors.surface.light};
  border-radius: ${e=>e.theme.borderRadius.pill};
  overflow: hidden;
  position: relative;
  border: 1px solid ${e=>e.theme.colors.surface.light};
`,lc=a.div`
  height: 100%;
  width: ${e=>Math.min(e.progress,100)}%;
  transition: width 0.5s ease-in-out;
  border-radius: ${e=>e.theme.borderRadius.pill};
  position: relative;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 2px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 0 ${e=>e.theme.borderRadius.pill} ${e=>e.theme.borderRadius.pill} 0;
  }
`,cc=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  color: ${e=>e.theme.colors.text.secondary};
`,Un=a.span`
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,er=({title:e,current:r,target:n,unit:o="",color:s="neonCarrot",size:i="md",showPercentage:l=!0,className:c})=>{const m=n>0?r/n*100:0,d=Math.round(m),h=r>=n;return t.jsxs(ac,{color:s,size:i,className:c,children:[t.jsx(sc,{className:"chart-title",children:e}),t.jsx(ic,{children:t.jsx(lc,{className:"progress-fill",progress:m})}),t.jsxs(cc,{className:"progress-stats",children:[t.jsxs("div",{children:[t.jsx(Un,{className:"progress-text",children:r}),o&&` ${o}`," / ",n,o&&` ${o}`]}),l&&t.jsxs("div",{className:"progress-text",children:[t.jsxs(Un,{children:[d,"%"]}),h&&" ✓"]})]})]})},dc={neonCarrot:z`
    .estimate-value {
      color: ${e=>e.theme.colors.primary.neonCarrot};
    }
    .estimate-border {
      border-color: ${e=>e.theme.colors.primary.neonCarrot};
    }
  `,lilac:z`
    .estimate-value {
      color: ${e=>e.theme.colors.primary.lilac};
    }
    .estimate-border {
      border-color: ${e=>e.theme.colors.primary.lilac};
    }
  `,anakiwa:z`
    .estimate-value {
      color: ${e=>e.theme.colors.primary.anakiwa};
    }
    .estimate-border {
      border-color: ${e=>e.theme.colors.primary.anakiwa};
    }
  `,success:z`
    .estimate-value {
      color: ${e=>e.theme.colors.status.success};
    }
    .estimate-border {
      border-color: ${e=>e.theme.colors.status.success};
    }
  `},pc={sm:z`
    .estimate-title {
      font-size: ${e=>e.theme.typography.fontSize.xs};
    }
    .estimate-value {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
    .estimate-subtitle {
      font-size: ${e=>e.theme.typography.fontSize.xs};
    }
    padding: ${e=>e.theme.spacing.sm};
  `,md:z`
    .estimate-title {
      font-size: ${e=>e.theme.typography.fontSize.sm};
    }
    .estimate-value {
      font-size: ${e=>e.theme.typography.fontSize.lg};
    }
    .estimate-subtitle {
      font-size: ${e=>e.theme.typography.fontSize.sm};
    }
    padding: ${e=>e.theme.spacing.md};
  `,lg:z`
    .estimate-title {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
    .estimate-value {
      font-size: ${e=>e.theme.typography.fontSize.xl};
    }
    .estimate-subtitle {
      font-size: ${e=>e.theme.typography.fontSize.md};
    }
    padding: ${e=>e.theme.spacing.lg};
  `},mc=a.div`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid;
  border-radius: ${e=>e.theme.borderRadius.lg};
  text-align: center;
  position: relative;

  ${e=>dc[e.color]}
  ${e=>pc[e.size]}

  ${e=>e.isComplete&&z`
    .estimate-value {
      color: ${r=>r.theme.colors.status.success};
    }
    .estimate-border {
      border-color: ${r=>r.theme.colors.status.success};
    }

    &::after {
      content: '✓ COMPLETE';
      position: absolute;
      top: 8px;
      right: 8px;
      font-size: ${r=>r.theme.typography.fontSize.xs};
      color: ${r=>r.theme.colors.status.success};
      font-weight: ${r=>r.theme.typography.fontWeight.bold};
      text-transform: uppercase;
      letter-spacing: 1px;
    }
  `}
`,hc=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  color: ${e=>e.theme.colors.text.secondary};
  margin-bottom: ${e=>e.theme.spacing.sm};
`,ur=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  line-height: ${e=>e.theme.typography.lineHeight.tight};
  margin-bottom: ${e=>e.theme.spacing.xs};
`,gr=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`,xr=({title:e,estimatedDate:r,daysRemaining:n,isComplete:o=!1,color:s="neonCarrot",size:i="md",className:l})=>{const c=d=>{try{return new Date(d).toLocaleDateString("en-US",{year:"numeric",month:"short",day:"numeric"})}catch{return"Unknown"}},m=d=>{if(d<=0)return"Goal Achieved";if(d===1)return"1 Day";if(d<30)return`${d} Days`;if(d<365){const y=Math.round(d/30);return y===1?"1 Month":`${y} Months`}const h=Math.round(d/365);return h===1?"1 Year":`${h} Years`};return t.jsxs(mc,{color:s,size:i,isComplete:o,className:`estimate-border ${l||""}`,children:[t.jsx(hc,{className:"estimate-title",children:e}),o?t.jsxs(t.Fragment,{children:[t.jsx(ur,{className:"estimate-value",children:"ACHIEVED"}),t.jsx(gr,{className:"estimate-subtitle",children:"Goal Complete"})]}):t.jsxs(t.Fragment,{children:[r&&t.jsxs(t.Fragment,{children:[t.jsx(ur,{className:"estimate-value",children:c(r)}),t.jsx(gr,{className:"estimate-subtitle",children:"Estimated Completion"})]}),n!==void 0&&t.jsxs(t.Fragment,{children:[t.jsx(ur,{className:"estimate-value",children:m(n)}),t.jsx(gr,{className:"estimate-subtitle",children:"Remaining"})]})]})]})},je={all:["boats"],lists:()=>[...je.all,"list"],list:e=>[...je.lists(),{filters:e}],details:()=>[...je.all,"detail"],detail:e=>[...je.details(),e]},ye=()=>he({queryKey:je.lists(),queryFn:()=>O.getBoats()}),uc=e=>he({queryKey:je.detail(e),queryFn:()=>O.getBoat(e),enabled:!!e}),gc=()=>{const e=Y();return ee({mutationFn:r=>O.createBoat(r),onSuccess:()=>{e.invalidateQueries({queryKey:je.lists()})}})},xc=()=>{const e=Y();return ee({mutationFn:({id:r,data:n})=>O.updateBoat(r,n),onSuccess:(r,{id:n})=>{e.invalidateQueries({queryKey:je.detail(n)}),e.invalidateQueries({queryKey:je.lists()})}})},wa=()=>{const e=Y();return ee({mutationFn:({id:r,enabled:n})=>O.toggleBoatStatus(r,n),onSuccess:(r,{id:n})=>{e.invalidateQueries({queryKey:je.detail(n)}),e.invalidateQueries({queryKey:je.lists()})}})},Ca=()=>{const e=Y();return ee({mutationFn:r=>O.setActiveBoat(r),onSuccess:()=>{e.invalidateQueries({queryKey:je.lists()})}})},Ce={all:["trips"],lists:()=>[...Ce.all,"list"],list:e=>[...Ce.lists(),{filters:e}],details:()=>[...Ce.all,"detail"],detail:e=>[...Ce.details(),e]},_e=e=>he({queryKey:Ce.list(e||{}),queryFn:()=>O.getTrips(e)}),Sa=e=>he({queryKey:Ce.detail(e),queryFn:()=>O.getTrip(e),enabled:!!e}),fc=()=>{const e=Y();return ee({mutationFn:r=>O.createTrip(r),onSuccess:()=>{e.invalidateQueries({queryKey:Ce.lists()})}})},yc=()=>{const e=Y();return ee({mutationFn:({id:r,data:n})=>O.updateTrip(r,n),onSuccess:(r,{id:n})=>{e.invalidateQueries({queryKey:Ce.detail(n)}),e.invalidateQueries({queryKey:Ce.lists()})}})},bc=()=>{const e=Y();return ee({mutationFn:({tripId:r,data:n})=>O.addManualData(r,n),onSuccess:(r,{tripId:n})=>{e.invalidateQueries({queryKey:Ce.detail(n)}),e.invalidateQueries({queryKey:Ce.lists()})}})},ka={all:["license"],progress:()=>[...ka.all,"progress"]},Ta=()=>he({queryKey:ka.progress(),queryFn:()=>O.getLicenseProgress(),staleTime:5*60*1e3}),jc=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
`,vc=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,$c=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${e=>e.theme.spacing.lg};
  
  @media (max-width: ${e=>e.theme.breakpoints.md}) {
    grid-template-columns: 1fr;
  }
`,wc=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${e=>e.theme.spacing.sm};
  border-bottom: 1px solid ${e=>e.theme.colors.surface.light};
  
  &:last-child {
    border-bottom: none;
  }
`,Cc=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,Sc=a.span`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
`,kc=a.span`
  color: ${e=>e.theme.colors.text.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,Tc=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  flex-wrap: wrap;
`,Ac=a.div`
  width: 100%;
  height: 8px;
  background-color: ${e=>e.theme.colors.surface.light};
  border-radius: ${e=>e.theme.borderRadius.pill};
  overflow: hidden;
  margin-top: ${e=>e.theme.spacing.sm};

  &::after {
    content: '';
    display: block;
    width: ${e=>Math.min(e.progress,100)}%;
    height: 100%;
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    transition: width ${e=>e.theme.animation.normal} ease;
  }
`,Fc=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: ${e=>e.theme.spacing.xs};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
`,Ec=()=>{const e=de(),{data:r,isLoading:n,error:o}=ye(),{data:s,isLoading:i,error:l}=_e(),{data:c,isLoading:m,error:d}=Ta(),h=(r==null?void 0:r.filter(u=>u.enabled))||[],y=(s==null?void 0:s.slice(0,5))||[],j=(s==null?void 0:s.length)||0,f=u=>new Date(u).toLocaleDateString("en-US",{month:"short",day:"numeric",year:"numeric"}),p=u=>{const x=Math.floor(u/3600),v=Math.floor(u%3600/60);return`${x}h ${v}m`},g=(u,x)=>Math.min(u/x*100,100);return t.jsxs(jc,{children:[t.jsx(q,{level:1,children:"Command Center"}),(o||l||d)&&t.jsx(Se,{type:"error",children:"Unable to load dashboard data. Check your connection and try again."}),t.jsxs(vc,{children:[t.jsx(L,{title:"Fleet Status",variant:"accent",children:n?t.jsx(E,{label:"Loading",value:"...",valueColor:"anakiwa"}):t.jsxs(t.Fragment,{children:[t.jsx(E,{label:"Total Vessels",value:(r==null?void 0:r.length)||0,valueColor:"anakiwa"}),t.jsx(E,{label:"Active Vessels",value:h.length,valueColor:"success"}),t.jsx(E,{label:"Inactive Vessels",value:((r==null?void 0:r.length)||0)-h.length,valueColor:"neonCarrot"})]})}),t.jsx(L,{title:"License Progress",variant:"secondary",children:m?t.jsx(E,{label:"Loading",value:"...",valueColor:"lilac"}):c?t.jsxs(t.Fragment,{children:[t.jsx(E,{label:"Sea Time Days",value:c.totalDays,valueColor:"lilac"}),t.jsx(E,{label:"Days (3 Years)",value:c.daysInLast3Years,valueColor:"lilac"}),t.jsxs("div",{children:[t.jsx(Ac,{progress:g(c.totalDays,360)}),t.jsxs(Fc,{children:[t.jsx("span",{children:"360 Day Goal"}),t.jsxs("span",{children:[Math.round(g(c.totalDays,360)),"%"]})]})]})]}):t.jsx(E,{label:"Status",value:"Disabled",valueColor:"neonCarrot"})}),t.jsxs(L,{title:"System Status",variant:"primary",children:[t.jsx(E,{label:"Interface Status",value:"ONLINE",valueColor:"success",size:"sm"}),t.jsx(E,{label:"Active Boats",value:n?"...":h.length.toString(),valueColor:"neonCarrot",size:"sm"}),t.jsx(E,{label:"Total Trips",value:i?"...":j.toString(),valueColor:"anakiwa",size:"sm"})]})]}),t.jsxs(Tc,{children:[t.jsx(k,{size:"sm",variant:"primary",onClick:()=>e("/trips"),children:"View Trips"}),t.jsx(k,{size:"sm",variant:"secondary",onClick:()=>e("/boats/new"),children:"Add Boat"})]}),t.jsxs($c,{children:[t.jsx(L,{title:"Recent Trips",variant:"primary",children:i?t.jsx(E,{label:"Loading",value:"...",valueColor:"neonCarrot"}):y.length>0?y.map(u=>{var x,v;return t.jsxs(wc,{children:[t.jsxs(Cc,{children:[t.jsx(Sc,{children:f(u.startTime)}),t.jsxs(kc,{children:[p(((x=u.statistics)==null?void 0:x.durationSeconds)||0)," • ",u.waterType]})]}),t.jsx(E,{label:"Distance",value:Math.round((((v=u.statistics)==null?void 0:v.distanceMeters)||0)/1852),unit:"nm",size:"sm",valueColor:"neonCarrot"})]},u.id)}):t.jsx("div",{style:{textAlign:"center",padding:"2rem",color:"#999"},children:"No trips recorded yet"})}),t.jsx(L,{title:"Upcoming Tasks",variant:"accent",children:t.jsx("div",{style:{textAlign:"center",padding:"2rem",color:"#999"},children:"No maintenance tasks due"})})]})]})},Lc=ie`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
`,zc=ie`
  0%, 100% {
    filter: drop-shadow(0 0 20px rgba(255, 153, 51, 0.6));
  }
  50% {
    filter: drop-shadow(0 0 40px rgba(255, 153, 51, 0.9)) drop-shadow(0 0 60px rgba(255, 153, 51, 0.4));
  }
`,Dc=ie`
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
`,Aa=ie`
  0%, 49% { opacity: 1; }
  50%, 100% { opacity: 0; }
`,Qe=ie`
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
`,Ic=ie`
  from { transform: translateX(-200px); opacity: 0; }
  to { transform: translateX(0); opacity: 0.7; }
`,Mc=a.div`
  min-height: 100vh;
  background: #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  padding: 2rem;
`,Rc=a.canvas`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  opacity: 0.4;
`,Nc=a.img`
  width: 400px;
  max-width: 80vw;
  height: auto;
  margin-bottom: 3rem;
  animation: ${zc} 3s ease-in-out infinite, ${Qe} 1s ease;
  cursor: pointer;
  transition: transform 0.3s ease;
  z-index: 1;

  &:hover {
    transform: scale(1.05);
  }

  @media (max-width: 768px) {
    width: 250px;
    margin-bottom: 2rem;
  }

  @media (max-width: 480px) {
    width: 200px;
    margin-bottom: 1.5rem;
  }
`,Pc=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
  margin: 2rem 0;
  z-index: 1;
  animation: ${Qe} 1s ease 0.3s backwards;
  position: relative;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
`,Bc=a.div`
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid ${e=>e.$color};
  border-radius: 0 16px 16px 0;
  padding: 1rem 1.5rem;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 6px;
    background: ${e=>e.$color};
  }
`,Oc=a.div`
  color: #99CCFF;
  font-family: 'Antonio', sans-serif;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 0.5rem;
`,Uc=a.div`
  color: ${e=>e.$color};
  font-family: 'Antonio', sans-serif;
  font-size: 1.5rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
`,qc=a.div`
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
  margin-top: 0.5rem;
`,Hc=a.div`
  height: 100%;
  background: ${e=>e.$color};
  width: ${e=>e.$percentage}%;
  transition: width 0.3s ease;
  box-shadow: 0 0 8px ${e=>e.$color};
`,Wc=a.div`
  position: absolute;
  left: -100px;
  top: ${e=>e.$top};
  width: 80px;
  height: 4px;
  background: ${e=>e.$color};
  border-radius: 2px;
  opacity: 0;
  animation: ${Ic} 1.5s ease-in-out ${e=>e.$delay}s forwards;
  z-index: 0;
`,Vc=a.div`
  position: absolute;
  top: 100px;
  left: 40px;
  width: 150px;
  height: 150px;
  border-top: 12px solid #FFCC99;
  border-left: 12px solid #FFCC99;
  border-radius: 40px 0 0 0;
  z-index: 0;
  opacity: 0.6;
  animation: ${Qe} 1s ease 0.5s backwards;

  @media (max-width: 768px) {
    width: 80px;
    height: 80px;
    top: 60px;
    left: 20px;
    border-width: 8px;
  }
`,Kc=a.div`
  position: absolute;
  bottom: 100px;
  right: 40px;
  width: 150px;
  height: 150px;
  border-bottom: 12px solid #CC99CC;
  border-right: 12px solid #CC99CC;
  border-radius: 0 0 40px 0;
  z-index: 0;
  opacity: 0.6;
  animation: ${Qe} 1s ease 0.7s backwards;

  @media (max-width: 768px) {
    width: 80px;
    height: 80px;
    bottom: 60px;
    right: 20px;
    border-width: 8px;
  }
`,Gc=a.div`
  display: flex;
  gap: 1rem;
  margin: 2rem 0;
  z-index: 1;
  animation: ${Qe} 1s ease 0.5s backwards;
`,_c=a.div`
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: ${e=>e.$color};
  box-shadow: 0 0 10px ${e=>e.$color};
  animation: ${Lc} 2s ease-in-out ${e=>e.$delay} infinite;
`,Qc=a.div`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  z-index: 1;
  flex-wrap: wrap;
  justify-content: center;
  animation: ${Qe} 1s ease 0.7s backwards;

  @media (max-width: 768px) {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
  }
`,fr=a.button`
  background: ${e=>e.$color};
  color: #000;
  border: none;
  padding: 1rem 2rem;
  font-family: 'Antonio', sans-serif;
  font-size: 1.125rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-radius: 0 24px 24px 0;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.3);
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  &:hover {
    filter: brightness(1.2);
    box-shadow: 0 0 20px ${e=>e.$color};
  }

  &:hover::after {
    transform: translateX(0);
  }

  &:active {
    transform: scale(0.98);
  }
`,Jc=a.div`
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: rgba(102, 68, 102, 0.9);
  border-top: 3px solid #FFCC66;
  overflow: hidden;
  display: flex;
  align-items: center;
  z-index: 2;
`,Yc=a.div`
  display: flex;
  white-space: nowrap;
  animation: ${Dc} 40s linear infinite;
  gap: 3rem;
`,Zc=a.span`
  color: #FFCC99;
  font-family: 'Antonio', sans-serif;
  font-size: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  font-weight: 600;

  &::before {
    content: '●';
    color: #FF9933;
    margin-right: 1rem;
    animation: ${Aa} 1.5s ease-in-out infinite;
  }
`,Xc=a.div`
  position: absolute;
  top: 2rem;
  right: 2rem;
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid #99CCFF;
  border-radius: 0 16px 16px 0;
  padding: 0.75rem 1.5rem;
  z-index: 1;
  animation: ${Qe} 1s ease 0.9s backwards;

  @media (max-width: 768px) {
    top: 1rem;
    right: 1rem;
    padding: 0.5rem 1rem;
  }
`,ed=a.div`
  color: #99CCFF;
  font-family: 'Antonio', sans-serif;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 0.25rem;
`,td=a.div`
  color: #FFCC66;
  font-family: 'Antonio', sans-serif;
  font-size: 1.25rem;
  font-weight: bold;
  letter-spacing: 0.05em;
`,rd=a.div`
  position: absolute;
  top: 2rem;
  left: 2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid #FF9933;
  border-radius: 0 16px 16px 0;
  padding: 0.5rem 1rem;
  z-index: 1;
  animation: ${Qe} 1s ease 0.9s backwards;

  @media (max-width: 768px) {
    top: 1rem;
    left: 1rem;
    padding: 0.4rem 0.8rem;
  }
`,nd=a.span`
  color: #FF9933;
  font-family: 'Antonio', sans-serif;
  font-size: 0.875rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.15em;
`,od=a.div`
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #FF9933;
  box-shadow: 0 0 10px #FF9933;
  animation: ${Aa} 1s ease-in-out infinite;
`;function qn(){const e=new Date,r=e.getFullYear(),n=new Date(r,0,1).getTime(),o=new Date(r+1,0,1).getTime(),s=(e.getTime()-n)/(o-n);return((r-2323)*1e3+s*1e3).toFixed(5)}function fe(e,r){return e+Math.random()*(r-e)}const ad=()=>{const e=b.useRef(null),r=b.useRef([]),n=b.useRef();return b.useEffect(()=>{const o=e.current;if(!o)return;const s=o.getContext("2d");if(!s)return;const i=()=>{o.width=window.innerWidth,o.height=window.innerHeight};i(),window.addEventListener("resize",i);const l=200,c=[];for(let d=0;d<l;d++)c.push({x:fe(-o.width,o.width),y:fe(-o.height,o.height),z:fe(0,o.width)});r.current=c;const m=()=>{const d=o.width,h=o.height,y=d/2,j=h/2;s.fillStyle="rgba(0, 0, 0, 0.1)",s.fillRect(0,0,d,h),c.forEach(f=>{f.z-=2,f.z<=0&&(f.x=fe(-d,d),f.y=fe(-h,h),f.z=d,f.prevX=void 0,f.prevY=void 0);const p=128/f.z,g=f.x*p+y,u=f.y*p+j;if(g>=0&&g<=d&&u>=0&&u<=h){const x=(1-f.z/d)*2,v=Math.floor((1-f.z/d)*255),$=.5+(1-f.z/d)*.5;f.prevX!==void 0&&f.prevY!==void 0&&(s.strokeStyle=`rgba(${v}, ${v}, 255, ${$*.5})`,s.lineWidth=x*.5,s.beginPath(),s.moveTo(f.prevX,f.prevY),s.lineTo(g,u),s.stroke()),s.fillStyle=`rgba(${v}, ${v}, 255, ${$})`,s.beginPath(),s.arc(g,u,x,0,Math.PI*2),s.fill(),f.prevX=g,f.prevY=u}}),n.current=requestAnimationFrame(m)};return m(),()=>{window.removeEventListener("resize",i),n.current&&cancelAnimationFrame(n.current)}},[]),t.jsx(Rc,{ref:e})},Hn=()=>{const e=de(),[r,n]=b.useState(qn()),[o,s]=b.useState(257.4),[i,l]=b.useState(1.247),[c,m]=b.useState(97.3),[d,h]=b.useState(1547.2),[y,j]=b.useState(.0042),[f,p]=b.useState(99.7),[g,u]=b.useState([]);b.useEffect(()=>{const w=setInterval(()=>{n(qn())},3e3);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{s(fe(250,280))},500);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{l(fe(1.1,1.4))},300);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{m(fe(94,100))},600);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{h(fe(1500,1600))},400);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{j(fe(.003,.006))},700);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=setInterval(()=>{p(fe(98.5,100))},550);return()=>clearInterval(w)},[]),b.useEffect(()=>{const w=["#FFCC99","#99CCFF","#CC99CC","#FFCC66","#FF9933"],T=setInterval(()=>{const I={color:w[Math.floor(Math.random()*w.length)],top:`${fe(20,80)}%`,delay:0};u(D=>[...D,I].slice(-6))},fe(3e3,6e3));return()=>clearInterval(T)},[]);const x=["All systems nominal","Warp core stable","Navigation array calibrated","Subspace communications active","Deflector shields online","Sensors operating at peak efficiency","Life support systems optimal","Transporter standing by","Quantum slipstream drive ready","Temporal sensors synchronized"],v=[{label:"Shield Harmonic Frequency",value:`${o.toFixed(1)} MHz`,color:"#99CCFF",percentage:(o-250)/30*100},{label:"Anti-Matter Injection Flow",value:`${i.toFixed(3)} cm³/s`,color:"#FFCC66",percentage:(i-1.1)/(1.4-1.1)*100},{label:"Communications Uplink Signal",value:`${c.toFixed(1)}%`,color:"#99CCFF",percentage:(c-94)/6*100},{label:"Warp Core Output",value:`${d.toLocaleString("en-US",{minimumFractionDigits:1,maximumFractionDigits:1})} TW`,color:"#FFCC99",percentage:(d-1500)/100*100},{label:"Sensor Array Resolution",value:`${y.toFixed(4)} arc-sec`,color:"#99CCFF",percentage:(y-.003)/(.006-.003)*100},{label:"Life Support Efficiency",value:`${f.toFixed(1)}%`,color:"#CC99CC",percentage:(f-98.5)/(100-98.5)*100}],$=[{color:"#FF9933",delay:"0s"},{color:"#99CCFF",delay:"0.3s"},{color:"#CC99CC",delay:"0.6s"},{color:"#FFCC66",delay:"0.9s"},{color:"#99CCFF",delay:"1.2s"}];return t.jsxs(Mc,{children:[t.jsx(ad,{}),t.jsxs(Xc,{children:[t.jsx(ed,{children:"Stardate"}),t.jsx(td,{children:r})]}),t.jsxs(rd,{children:[t.jsx(nd,{children:"LCARS"}),t.jsx(od,{})]}),t.jsx(Vc,{}),t.jsx(Kc,{}),t.jsx(Nc,{src:"/assets/captains-log-logo.png",alt:"Captain's Log",onClick:()=>e("/dashboard")}),t.jsx(Gc,{children:$.map((w,A)=>t.jsx(_c,{$color:w.color,$delay:w.delay},A))}),t.jsxs(Pc,{children:[g.map((w,A)=>t.jsx(Wc,{$color:w.color,$top:w.top,$delay:w.delay},A)),v.map((w,A)=>t.jsxs(Bc,{$color:w.color,children:[t.jsx(Oc,{children:w.label}),t.jsx(Uc,{$color:w.color,children:w.value}),t.jsx(qc,{children:t.jsx(Hc,{$color:w.color,$percentage:w.percentage})})]},A))]}),t.jsxs(Qc,{children:[t.jsx(fr,{$color:"#FFCC99",onClick:()=>e("/dashboard"),children:"Dashboard"}),t.jsx(fr,{$color:"#99CCFF",onClick:()=>e("/trips"),children:"Trip Log"}),t.jsx(fr,{$color:"#CC99CC",onClick:()=>e("/boats"),children:"Vessels"})]}),t.jsx(Jc,{children:t.jsx(Yc,{children:x.concat(x).map((w,A)=>t.jsx(Zc,{children:w},A))})})]})},sd=a.div`
  position: relative;
  display: flex;
  align-items: center;
`,id=a.input`
  width: 100%;
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  padding: ${e=>e.theme.spacing.md};
  padding-right: 48px;
  color: ${e=>e.theme.colors.text.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: ${e=>e.theme.shadows.glow};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`,ld=a.button`
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  color: ${e=>e.theme.colors.primary.anakiwa};
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  svg {
    width: 22px;
    height: 22px;
  }
`,cd=()=>t.jsxs("svg",{viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[t.jsx("path",{d:"M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"}),t.jsx("circle",{cx:"12",cy:"12",r:"3"})]}),dd=()=>t.jsxs("svg",{viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[t.jsx("path",{d:"M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"}),t.jsx("line",{x1:"1",y1:"1",x2:"23",y2:"23"})]}),Lt=({id:e,name:r,value:n,onChange:o,placeholder:s,required:i,disabled:l,minLength:c,autoComplete:m})=>{const[d,h]=b.useState(!1);return t.jsxs(sd,{children:[t.jsx(id,{type:d?"text":"password",id:e,name:r,value:n,onChange:o,placeholder:s,required:i,disabled:l,minLength:c,autoComplete:m}),t.jsx(ld,{type:"button",onClick:()=>h(!d),disabled:l,"aria-label":d?"Hide password":"Show password",children:d?t.jsx(dd,{}):t.jsx(cd,{})})]})};let xt=null;const pd={boats:["boats"],trips:["trips"],notes:["notes"],todos:["todos"],maintenance_templates:["maintenanceTemplates"],maintenance_events:["maintenanceEvents"],locations:["locations"],photos:["photos"],sensors:["sensors"]};function Wn(e){Fa();const r=localStorage.getItem("auth_token");if(!r)return;const o=`${localStorage.getItem("api_base_url")||void 0||"/api/v1"}/sync/events?token=${encodeURIComponent(r)}`;xt=new EventSource(o),xt.onmessage=s=>{try{const i=JSON.parse(s.data);if(i.type==="connected")return;const l=pd[i.type];l&&e.invalidateQueries({queryKey:l})}catch{}},xt.onerror=()=>{}}function Fa(){xt&&(xt.close(),xt=null)}const Ea=b.createContext(null),md=({children:e})=>{var m;const r=Y(),[n,o]=b.useState({isAuthenticated:!1,isLoading:!0,needsSetup:!1,user:null}),s=b.useCallback(async()=>{try{if(!localStorage.getItem("auth_token")){o({isAuthenticated:!1,isLoading:!1,needsSetup:!0,user:null});return}await O.getBoats(),o({isAuthenticated:!0,isLoading:!1,needsSetup:!1,user:{id:"current",username:"user",role:localStorage.getItem("user_role")||"ADMIN",createdAt:"",updatedAt:""}}),Wn(r)}catch{localStorage.removeItem("auth_token"),o({isAuthenticated:!1,isLoading:!1,needsSetup:!0,user:null})}},[]);b.useEffect(()=>{s()},[s]);const i=b.useCallback(async(d,h)=>{var y;try{const j=await O.login(d,h);return o({isAuthenticated:!0,isLoading:!1,needsSetup:!1,user:j.user}),(y=j.user)!=null&&y.role&&localStorage.setItem("user_role",j.user.role),Wn(r),{success:!0}}catch(j){return o(f=>({...f,isAuthenticated:!1})),{success:!1,error:j.message||"Login failed"}}},[]),l=b.useCallback(async()=>{try{await O.logout()}catch(d){console.warn("Logout request failed:",d)}finally{Fa(),localStorage.removeItem("user_role"),o({isAuthenticated:!1,isLoading:!1,needsSetup:!1,user:null})}},[]),c={...n,isReadOnly:((m=n.user)==null?void 0:m.role)==="VIEWER",login:i,logout:l,checkAuthStatus:s};return Ke.createElement(Ea.Provider,{value:c},e)},sr=()=>{const e=b.useContext(Ea);if(!e)throw new Error("useAuth must be used within an AuthProvider");return e},hd=a.div`
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: ${e=>e.theme.colors.background};
  padding: ${e=>e.theme.spacing.lg};
`,ud=a.div`
  max-width: 600px;
  width: 100%;
`,gd=a.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${e=>e.theme.spacing.xl};
`,xd=a.img`
  max-width: 200px;
  height: auto;
  filter: drop-shadow(0 0 10px ${e=>e.theme.colors.primary.neonCarrot}40);
`,fd=a.form`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
`,yr=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,br=a.label`
  color: ${e=>e.theme.colors.text.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
`,Vn=a.input`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  padding: ${e=>e.theme.spacing.md};
  color: ${e=>e.theme.colors.text.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: ${e=>e.theme.shadows.glow};
  }
`,yd=a.div`
  display: flex;
  justify-content: center;
  margin-top: ${e=>e.theme.spacing.lg};
`,jr=()=>{const e=de(),{login:r,isAuthenticated:n}=sr();b.useEffect(()=>{n&&e("/")},[n,e]);const[o,s]=b.useState({username:"",password:"",serverUrl:""}),[i,l]=b.useState(!1),[c,m]=b.useState(null),[d,h]=b.useState(!1),y=f=>{const{name:p,value:g}=f.target;s(u=>({...u,[p]:g}))},j=async f=>{f.preventDefault(),l(!0),m(null);try{o.serverUrl.trim()?(O.updateBaseUrl(o.serverUrl),console.log("Server URL configured:",o.serverUrl)):console.log("Using default server URL (proxy)"),console.log("Attempting login with:",{username:o.username});const p=await r(o.username,o.password);console.log("Login result:",p),p.success?(m({type:"success",text:"LCARS Interface Initialized Successfully! Redirecting..."}),console.log("Login successful, setting timeout for redirect"),setTimeout(()=>{console.log("Redirecting to dashboard"),e("/")},1500)):(console.log("Login failed:",p.error),m({type:"error",text:p.error||"Authentication failed. Please check your credentials."}))}catch(p){console.error("Login error:",p),m({type:"error",text:p.message||"Setup failed. Please check your connection and try again."})}finally{l(!1)}};return t.jsx(hd,{children:t.jsxs(ud,{children:[t.jsx(gd,{children:t.jsx(xd,{src:"/assets/captains-log-logo.png",alt:"Captain's Log"})}),t.jsxs(L,{title:"System Initialization",padding:"lg",children:[t.jsx(q,{level:2,align:"center",children:"LCARS Setup Wizard"}),t.jsxs(fd,{onSubmit:j,children:[t.jsxs(yr,{children:[t.jsx(br,{htmlFor:"username",children:"Username"}),t.jsx(Vn,{type:"text",id:"username",name:"username",value:o.username,onChange:y,placeholder:"Enter your username",required:!0,disabled:i})]}),t.jsxs(yr,{children:[t.jsx(br,{htmlFor:"password",children:"Password"}),t.jsx(Lt,{id:"password",name:"password",value:o.password,onChange:y,placeholder:"Enter your password",required:!0,disabled:i})]}),t.jsx("div",{style:{textAlign:"right"},children:t.jsx("button",{type:"button",onClick:()=>h(!d),style:{background:"none",border:"none",color:"#99CCFF",cursor:"pointer",fontSize:"12px",textTransform:"uppercase",letterSpacing:"1px"},children:d?"Hide Advanced":"Advanced Options"})}),d&&t.jsxs(yr,{children:[t.jsx(br,{htmlFor:"serverUrl",children:"Server URL (Optional)"}),t.jsx(Vn,{type:"url",id:"serverUrl",name:"serverUrl",value:o.serverUrl,onChange:y,placeholder:"Leave empty for default",disabled:i})]}),c&&t.jsx(Se,{type:c.type==="success"?"success":c.type==="error"?"error":"info",children:c.text}),t.jsx(yd,{children:t.jsx(k,{type:"submit",disabled:i,size:"lg",children:i?"Initializing...":"Initialize LCARS"})})]})]})]})})},bd=ie`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`,jd=ie`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
`;a.div`
  display: flex;
  align-items: center;
  justify-content: center;
  ${e=>e.fullScreen&&`
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    z-index: 9999;
  `}
  ${e=>!e.fullScreen&&`
    padding: ${e.theme.spacing.xl};
  `}
`;a.div`
  width: ${e=>{switch(e.size){case"sm":return"20px";case"lg":return"60px";default:return"40px"}}};
  height: ${e=>{switch(e.size){case"sm":return"20px";case"lg":return"60px";default:return"40px"}}};
  border: 3px solid ${e=>e.theme.colors.primary.neonCarrot}40;
  border-top: 3px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: 50%;
  animation: ${bd} 1s linear infinite;
`;a.div`
  margin-left: ${e=>e.theme.spacing.md};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>{switch(e.size){case"sm":return e.theme.typography.fontSize.sm;case"lg":return e.theme.typography.fontSize.lg;default:return e.theme.typography.fontSize.md}}};
  animation: ${jd} 2s ease-in-out infinite;
`;a.div`
  width: 200px;
  height: 20px;
  background: ${e=>e.theme.colors.surface.dark};
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      ${e=>e.theme.colors.primary.neonCarrot},
      transparent
    );
    animation: ${ie`
      0% { left: -100%; }
      100% { left: 100%; }
    `} 2s ease-in-out infinite;
  }
`;const La=a.div`
  background: linear-gradient(
    90deg,
    ${e=>e.theme.colors.surface.dark} 25%,
    ${e=>e.theme.colors.surface.medium} 50%,
    ${e=>e.theme.colors.surface.dark} 75%
  );
  background-size: 200% 100%;
  animation: ${ie`
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
  `} 2s ease-in-out infinite;
  border-radius: 4px;
`,Kn=a(La)`
  width: ${e=>e.width||"100%"};
  height: ${e=>e.height||"1em"};
  margin: 4px 0;
`,vd=a(La)`
  width: 100%;
  height: 120px;
  margin: 8px 0;
`,Pt=({variant:e="text",width:r,height:n,lines:o=1})=>e==="card"?t.jsx(vd,{}):o===1?t.jsx(Kn,{width:r,height:n}):t.jsx("div",{children:Array.from({length:o},(s,i)=>t.jsx(Kn,{width:i===o-1?"60%":r,height:n},i))}),$d=a.div`
  ${e=>{switch(e.variant){case"inline":return`
          display: inline-flex;
          align-items: center;
          padding: ${e.theme.spacing.sm};
          background: ${e.theme.colors.status.error}20;
          border: 1px solid ${e.theme.colors.status.error};
          border-radius: 4px;
          color: ${e.theme.colors.status.error};
        `;case"banner":return`
          width: 100%;
          padding: ${e.theme.spacing.md};
          background: ${e.theme.colors.status.error}20;
          border-left: 4px solid ${e.theme.colors.status.error};
          color: ${e.theme.colors.status.error};
        `;default:return`
          padding: ${e.theme.spacing.lg};
          text-align: center;
        `}}}
`,wd=a.div`
  font-size: 1.2em;
  margin-right: ${e=>e.theme.spacing.sm};
  color: ${e=>e.theme.colors.status.error};
`,Cd=a.div`
  font-weight: bold;
  font-size: ${e=>e.theme.typography.fontSize.lg};
  color: ${e=>e.theme.colors.status.error};
  margin-bottom: ${e=>e.theme.spacing.sm};
`,Sd=a.div`
  color: ${e=>e.theme.colors.text.light};
  margin-bottom: ${e=>e.theme.spacing.md};
  line-height: 1.5;
`,kd=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.sm};
  justify-content: center;
  margin-top: ${e=>e.theme.spacing.md};
`,Td=a.code`
  background: ${e=>e.theme.colors.surface.dark};
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.9em;
  color: ${e=>e.theme.colors.status.error};
`,Ad=({title:e="Error",message:r,code:n,variant:o="card",showIcon:s=!0,onRetry:i,onDismiss:l,retryText:c="Try Again",dismissText:m="Dismiss"})=>{const d=t.jsxs($d,{variant:o,children:[s&&o==="inline"&&t.jsx(wd,{children:"⚠"}),o!=="inline"&&t.jsxs(Cd,{children:[s&&"⚠ ",e]}),t.jsxs(Sd,{children:[r,n&&t.jsxs(t.Fragment,{children:[t.jsx("br",{}),t.jsxs("small",{children:["Error code: ",t.jsx(Td,{children:n})]})]})]}),(i||l)&&t.jsxs(kd,{children:[i&&t.jsx(k,{onClick:i,variant:"primary",size:"sm",children:c}),l&&t.jsx(k,{onClick:l,variant:"secondary",size:"sm",children:m})]})]});return o==="card"?t.jsx(L,{children:d}):d};function Fd(e){const r=Y(),[n,o]=b.useState(!1);return{optimisticUpdate:b.useCallback(async(i,l,c,m)=>{o(!0);const d=r.getQueryData(e);r.setQueryData(e,h=>h===void 0?h:i(h));try{const h=await l();return await r.invalidateQueries({queryKey:e}),c==null||c(h),h}catch(h){throw d!==void 0&&r.setQueryData(e,d),m==null||m(h),h}finally{o(!1)}},[r,e]),isOptimistic:n}}function Ed(e){const{optimisticUpdate:r,isOptimistic:n}=Fd(e),o=b.useCallback((l,c)=>r((m=[])=>[...m,l],c),[r]),s=b.useCallback((l,c)=>r((m=[])=>m.filter(d=>d.id!==l),c),[r]),i=b.useCallback((l,c,m)=>r((d=[])=>d.map(h=>h.id===l?c(h):h),m),[r]);return{optimisticAdd:o,optimisticRemove:s,optimisticUpdate:i,isOptimistic:n}}const Ld=a.div`
  position: relative;
  opacity: 0.4;
  pointer-events: none;
  cursor: not-allowed;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
`,zd=a.div`
  position: relative;
  display: inline-block;

  &:hover > .readonly-tooltip {
    visibility: visible;
    opacity: 1;
  }
`,Dd=a.div`
  visibility: hidden;
  opacity: 0;
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 8px;
  background: ${e=>e.theme.colors.surface.dark};
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  text-transform: uppercase;
  letter-spacing: 1px;
  white-space: nowrap;
  z-index: 100;
  transition: opacity 0.2s;
  pointer-events: none;
`,Q=({children:e,fallback:r})=>{const{isReadOnly:n}=sr();return n?r!==void 0?t.jsx(t.Fragment,{children:r}):t.jsxs(zd,{children:[t.jsx(Ld,{children:e}),t.jsx(Dd,{className:"readonly-tooltip",children:"View Only"})]}):t.jsx(t.Fragment,{children:e})},vr=a.div`
  padding: 20px;
`,Gn=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-top: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Id=a.div`
  padding: 20px;
  border: 2px solid ${e=>e.$isActive?e.theme.colors.primary.neonCarrot:e.$isEnabled?e.theme.colors.primary.anakiwa:e.theme.colors.interactive.disabled};
  background: ${e=>e.$isActive?`${e.theme.colors.primary.neonCarrot}15`:e.$isEnabled?`${e.theme.colors.primary.anakiwa}10`:`${e.theme.colors.interactive.disabled}10`};
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: ${e=>e.theme.borderRadius.lg};

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    background: ${e=>e.theme.colors.primary.neonCarrot}20;
  }
`,Md=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1.4rem;
  margin: 0 0 15px 0;
  text-transform: uppercase;
`,Rd=a.div`
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
`,_n=a.span`
  padding: 4px 12px;
  border-radius: 0;
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
  background: ${e=>{switch(e.$type){case"active":return e.theme.colors.primary.neonCarrot;case"enabled":return e.theme.colors.primary.anakiwa;case"disabled":return e.theme.colors.interactive.disabled;default:return e.theme.colors.interactive.disabled}}};
  color: ${e=>e.theme.colors.background};
`,Nd=a.div`
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
`,Qn=a(k)`
  flex: 1;
  min-width: 120px;
`,Jn=a.div`
  display: flex;
  gap: 15px;
  align-items: center;
`,Pd=a.div`
  text-align: center;
  padding: 60px 20px;
  color: ${e=>e.theme.colors.text.secondary};
`,Bd=a.div`
  font-size: 4rem;
  margin-bottom: 20px;
  color: ${e=>e.theme.colors.primary.anakiwa};
`,Yn=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`,Zn=a.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`,Od=()=>{const e=de(),{data:r,isLoading:n,error:o}=ye(),s=wa(),i=Ca(),[l,c]=b.useState(null),{optimisticUpdate:m}=Ed(["boats"]),d=u=>{e(`/boats/${u.id}`)},h=async u=>{c(`toggle-${u.id}`);try{await m(u.id,x=>({...x,enabled:!x.enabled}),()=>s.mutateAsync({id:u.id,enabled:!u.enabled}))}catch(x){console.error("Failed to toggle boat status:",x)}finally{c(null)}},y=async u=>{if(!u.isActive){c(`active-${u.id}`);try{await i.mutateAsync(u.id)}catch(x){console.error("Failed to set active boat:",x)}finally{c(null)}}},j=()=>{e("/boats/new")};if(n)return t.jsxs(vr,{children:[t.jsxs(Yn,{children:[t.jsxs(Zn,{children:[t.jsx(q,{children:"BOAT MANAGEMENT"}),t.jsx(Pt,{width:"200px",height:"20px"})]}),t.jsxs(Jn,{children:[t.jsx(Pt,{width:"150px",height:"40px"}),t.jsx(Pt,{width:"180px",height:"40px"})]})]}),t.jsx(Gn,{children:Array.from({length:3},(u,x)=>t.jsx(L,{children:t.jsx(Pt,{variant:"card"})},x))})]});if(o)return t.jsxs(vr,{children:[t.jsx(q,{children:"BOAT MANAGEMENT"}),t.jsx(Ad,{title:"Failed to Load Boats",message:o.message,onRetry:()=>window.location.reload()})]});const f=r==null?void 0:r.find(u=>u.isActive),p=(r==null?void 0:r.filter(u=>u.enabled))||[],g=(r==null?void 0:r.filter(u=>!u.enabled))||[];return t.jsxs(vr,{children:[t.jsxs(Yn,{children:[t.jsxs(Zn,{children:[t.jsx(q,{children:"BOAT MANAGEMENT"}),t.jsx(E,{label:"VESSELS REGISTERED",value:(r==null?void 0:r.length)||0,valueColor:"anakiwa",size:"sm"})]}),t.jsxs(Jn,{children:[t.jsx(E,{label:"ACTIVE VESSEL",value:(f==null?void 0:f.name)||"NONE SELECTED",valueColor:f?"neonCarrot":"anakiwa"}),t.jsx(Q,{children:t.jsx(k,{variant:"primary",onClick:j,children:"ADD NEW VESSEL"})})]})]}),!r||r.length===0?t.jsx(L,{children:t.jsxs(Pd,{children:[t.jsx(Bd,{children:"🚤"}),t.jsx("h3",{children:"NO VESSELS REGISTERED"}),t.jsx("p",{children:"Add your first vessel to begin tracking trips and maintenance."}),t.jsx(Q,{children:t.jsx(k,{variant:"primary",onClick:j,children:"ADD FIRST VESSEL"})})]})}):t.jsx(Gn,{children:r.map(u=>t.jsxs(Id,{$isActive:u.isActive,$isEnabled:u.enabled,onClick:()=>d(u),children:[t.jsx(Md,{children:u.name}),t.jsxs(Rd,{children:[u.isActive&&t.jsx(_n,{$type:"active",children:"ACTIVE"}),t.jsx(_n,{$type:u.enabled?"enabled":"disabled",children:u.enabled?"ENABLED":"DISABLED"})]}),t.jsx(E,{label:"VESSEL ID",value:u.id.slice(0,8).toUpperCase(),valueColor:"anakiwa",size:"sm"}),t.jsx(E,{label:"REGISTERED",value:new Date(u.createdAt).toLocaleDateString(),valueColor:"anakiwa",size:"sm"}),t.jsxs(Nd,{children:[!u.isActive&&u.enabled&&t.jsx(Q,{children:t.jsx(Qn,{variant:"secondary",onClick:()=>y(u),disabled:l===`active-${u.id}`,children:l===`active-${u.id}`?"SETTING...":"SET ACTIVE"})}),t.jsx(Q,{children:t.jsx(Qn,{variant:u.enabled?"danger":"accent",onClick:()=>h(u),disabled:l===`toggle-${u.id}`,children:l===`toggle-${u.id}`?"UPDATING...":u.enabled?"DISABLE":"ENABLE"})})]})]},u.id))}),r&&r.length>0&&t.jsxs("div",{style:{marginTop:"30px",display:"flex",gap:"20px"},children:[t.jsx(E,{label:"ENABLED VESSELS",value:p.length.toString(),valueColor:"anakiwa"}),t.jsx(E,{label:"DISABLED VESSELS",value:g.length.toString(),valueColor:"lilac"})]})]})},$r=a.div`
  padding: 20px;
`,Ud=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Xn=a(L)`
  padding: 25px;
`,wr=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1.2rem;
  margin: 0 0 20px 0;
  text-transform: uppercase;
  border-bottom: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding-bottom: 10px;
`,qd=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 25px;
`,eo=a.div`
  padding: 15px;
  text-align: center;
  border: 2px solid ${e=>{switch(e.$type){case"active":return e.theme.colors.primary.neonCarrot;case"enabled":return e.theme.colors.primary.anakiwa;case"disabled":return e.theme.colors.interactive.disabled;default:return e.theme.colors.interactive.disabled}}};
  background: ${e=>{switch(e.$type){case"active":return`${e.theme.colors.primary.neonCarrot}20`;case"enabled":return`${e.theme.colors.primary.anakiwa}15`;case"disabled":return`${e.theme.colors.interactive.disabled}15`;default:return`${e.theme.colors.interactive.disabled}15`}}};
`,to=a.div`
  font-size: 0.9rem;
  color: ${e=>e.theme.colors.text.secondary};
  margin-bottom: 5px;
  text-transform: uppercase;
`,ro=a.div`
  font-size: 1.1rem;
  font-weight: bold;
  color: ${e=>e.theme.colors.text.primary};
  text-transform: uppercase;
`,Hd=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 20px;
`,no=a(k)`
  margin-right: 15px;
`,Wd=a.form`
  display: flex;
  flex-direction: column;
  gap: 20px;
`,Vd=a.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`,Kd=a.label`
  color: ${e=>e.theme.colors.text.primary};
  font-size: 0.9rem;
  text-transform: uppercase;
  font-weight: bold;
`,Gd=a.input`
  padding: 12px 15px;
  background: ${e=>e.theme.colors.background};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1rem;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`,_d=a.div`
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 20px;
`,Qd=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
`,Jd=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`,Yd=a.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`,Zd=a(L)`
  padding: 25px;
  margin-top: 30px;
`,Xd=()=>{const{id:e}=Ge(),r=de(),{data:n,isLoading:o,error:s}=uc(e),{data:i}=_e({boatId:e}),l=xc(),c=wa(),m=Ca(),[d,h]=b.useState(!1),[y,j]=b.useState({name:""}),[f,p]=b.useState(null);Ke.useEffect(()=>{n&&j({name:n.name})},[n]);const g=()=>{r("/boats")},u=()=>{h(!0)},x=()=>{h(!1),n&&j({name:n.name})},v=async D=>{if(D.preventDefault(),!(!n||!y.name.trim())){p("save");try{await l.mutateAsync({id:n.id,data:{name:y.name.trim()}}),h(!1)}catch(U){console.error("Failed to update boat:",U)}finally{p(null)}}},$=async()=>{if(n){p("toggle");try{await c.mutateAsync({id:n.id,enabled:!n.enabled})}catch(D){console.error("Failed to toggle boat status:",D)}finally{p(null)}}},w=async()=>{if(!(!n||n.isActive)){p("active");try{await m.mutateAsync(n.id)}catch(D){console.error("Failed to set active boat:",D)}finally{p(null)}}};if(o)return t.jsxs($r,{children:[t.jsx(q,{children:"VESSEL DETAILS"}),t.jsx(E,{label:"STATUS",value:"LOADING VESSEL DATA...",valueColor:"anakiwa"})]});if(s||!n)return t.jsxs($r,{children:[t.jsx(q,{children:"VESSEL DETAILS"}),t.jsx(Se,{type:"error",children:(s==null?void 0:s.message)||"Vessel not found"}),t.jsx(no,{variant:"secondary",onClick:g,children:"BACK TO VESSELS"})]});const A=(i==null?void 0:i.length)||0,T=(i==null?void 0:i.reduce((D,U)=>{var F;return D+(((F=U.statistics)==null?void 0:F.durationSeconds)||0)},0))||0,I=(i==null?void 0:i.reduce((D,U)=>{var F;return D+(((F=U.statistics)==null?void 0:F.distanceMeters)||0)},0))||0;return t.jsx(t.Fragment,{children:t.jsxs($r,{children:[t.jsxs(Jd,{children:[t.jsxs(Yd,{children:[t.jsx(q,{children:"VESSEL DETAILS"}),t.jsx(E,{label:"VESSEL NAME",value:n.name,valueColor:"neonCarrot",size:"sm"})]}),t.jsxs("div",{children:[t.jsx(no,{variant:"secondary",onClick:g,children:"BACK TO VESSELS"}),!d&&t.jsx(Q,{children:t.jsx(k,{variant:"primary",onClick:u,children:"EDIT VESSEL"})})]})]}),t.jsxs(Ud,{children:[t.jsxs(Xn,{children:[t.jsx(wr,{children:"Vessel Information"}),d?t.jsxs(Wd,{onSubmit:v,children:[t.jsxs(Vd,{children:[t.jsx(Kd,{children:"Vessel Name"}),t.jsx(Gd,{type:"text",value:y.name,onChange:D=>j({...y,name:D.target.value}),placeholder:"Enter vessel name",required:!0,disabled:f==="save"})]}),t.jsxs(_d,{children:[t.jsx(k,{type:"button",variant:"secondary",onClick:x,disabled:f==="save",children:"CANCEL"}),t.jsx(k,{type:"submit",variant:"primary",disabled:f==="save"||!y.name.trim(),children:f==="save"?"SAVING...":"SAVE CHANGES"})]})]}):t.jsxs(t.Fragment,{children:[t.jsx(E,{label:"VESSEL NAME",value:n.name,valueColor:"neonCarrot"}),t.jsx(E,{label:"VESSEL ID",value:n.id,valueColor:"anakiwa"}),t.jsx(E,{label:"REGISTERED",value:new Date(n.createdAt).toLocaleString(),valueColor:"anakiwa"}),t.jsx(E,{label:"LAST UPDATED",value:new Date(n.updatedAt).toLocaleString(),valueColor:"anakiwa"})]})]}),t.jsxs(Xn,{children:[t.jsx(wr,{children:"Status & Actions"}),t.jsxs(qd,{children:[t.jsxs(eo,{$type:n.isActive?"active":"disabled",children:[t.jsx(to,{children:"Active Status"}),t.jsx(ro,{children:n.isActive?"ACTIVE":"INACTIVE"})]}),t.jsxs(eo,{$type:n.enabled?"enabled":"disabled",children:[t.jsx(to,{children:"Operational Status"}),t.jsx(ro,{children:n.enabled?"ENABLED":"DISABLED"})]})]}),!d&&t.jsx(Q,{children:t.jsxs(Hd,{children:[!n.isActive&&n.enabled&&t.jsx(k,{variant:"primary",onClick:w,disabled:f==="active",children:f==="active"?"SETTING...":"SET AS ACTIVE"}),t.jsx(k,{variant:n.enabled?"danger":"accent",onClick:$,disabled:f==="toggle",children:f==="toggle"?"UPDATING...":n.enabled?"DISABLE VESSEL":"ENABLE VESSEL"})]})})]})]}),t.jsxs(Zd,{children:[t.jsx(wr,{children:"Usage Statistics"}),t.jsxs(Qd,{children:[t.jsx(E,{label:"TOTAL TRIPS",value:A.toString(),valueColor:"anakiwa"}),t.jsx(E,{label:"TOTAL HOURS",value:`${(T/3600).toFixed(1)}`,unit:"hrs",valueColor:"anakiwa"}),t.jsx(E,{label:"TOTAL DISTANCE",value:`${(I*539957e-9).toFixed(1)}`,unit:"nm",valueColor:"anakiwa"}),t.jsx(E,{label:"LAST TRIP",value:i&&i.length>0?new Date(i[0].startTime).toLocaleDateString():"NO TRIPS",valueColor:"anakiwa"})]})]})]})})},ep=a.div`
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
`,tp=a(L)`
  padding: 30px;
  margin-top: 20px;
`,rp=a.form`
  display: flex;
  flex-direction: column;
  gap: 25px;
`,Je=a.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`,Ye=a.label`
  color: ${e=>e.theme.colors.text.primary};
  font-size: 1rem;
  text-transform: uppercase;
  font-weight: bold;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
`,st=a.input`
  padding: 15px 20px;
  background: ${e=>e.theme.colors.background};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    border-color: ${e=>e.theme.colors.interactive.disabled};
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.secondary};
    opacity: 0.7;
  }
`,np=a.textarea`
  padding: 15px 20px;
  background: ${e=>e.theme.colors.background};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1rem;
  min-height: 120px;
  resize: vertical;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    border-color: ${e=>e.theme.colors.interactive.disabled};
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.secondary};
    opacity: 0.7;
  }
`,He=a.p`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.4;
`,op=a.div`
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid ${e=>e.theme.colors.primary.anakiwa};
`,ap=a(k)`
  margin-right: 15px;
`,sp=a.span`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin-left: 5px;
`,Ze=a.div`
  color: ${e=>e.theme.colors.status.error};
  font-size: 0.9rem;
  margin-top: 5px;
`,ip=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`,lp=a.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`,cp=()=>{const e=de(),r=gc(),[n,o]=b.useState({name:"",description:"",hullNumber:"",manufacturer:"",model:"",year:"",length:""}),[s,i]=b.useState({}),[l,c]=b.useState(!1),m=()=>{e("/boats")},d=(j,f)=>{o(p=>({...p,[j]:f})),s[j]&&i(p=>({...p,[j]:void 0}))},h=()=>{const j={};return n.name.trim()?n.name.trim().length<2?j.name="Vessel name must be at least 2 characters":n.name.trim().length>100&&(j.name="Vessel name must be less than 100 characters"):j.name="Vessel name is required",n.description&&n.description.length>500&&(j.description="Description must be less than 500 characters"),n.hullNumber&&n.hullNumber.length>50&&(j.hullNumber="Hull number must be less than 50 characters"),n.manufacturer&&n.manufacturer.length>100&&(j.manufacturer="Manufacturer must be less than 100 characters"),n.model&&n.model.length>100&&(j.model="Model must be less than 100 characters"),n.year&&(!/^\d{4}$/.test(n.year)||parseInt(n.year)<1900||parseInt(n.year)>new Date().getFullYear()+1)&&(j.year="Year must be a valid 4-digit year"),n.length&&(!/^\d+(\.\d+)?$/.test(n.length)||parseFloat(n.length)<=0||parseFloat(n.length)>1e3)&&(j.length="Length must be a positive number (in feet)"),i(j),Object.keys(j).length===0},y=async j=>{if(j.preventDefault(),!!h()){c(!0);try{const f={};n.description.trim()&&(f.description=n.description.trim()),n.hullNumber.trim()&&(f.hullNumber=n.hullNumber.trim()),n.manufacturer.trim()&&(f.manufacturer=n.manufacturer.trim()),n.model.trim()&&(f.model=n.model.trim()),n.year.trim()&&(f.year=parseInt(n.year.trim())),n.length.trim()&&(f.lengthFeet=parseFloat(n.length.trim()));const p=await r.mutateAsync({name:n.name.trim(),metadata:Object.keys(f).length>0?f:void 0});e(`/boats/${p.id}`)}catch(f){console.error("Failed to create boat:",f)}finally{c(!1)}}};return t.jsxs(ep,{children:[t.jsxs(ip,{children:[t.jsxs(lp,{children:[t.jsx(q,{children:"ADD NEW VESSEL"}),t.jsx(He,{children:"Register a new vessel for tracking"})]}),t.jsx(ap,{variant:"secondary",onClick:m,children:"BACK TO VESSELS"})]}),r.error&&t.jsxs(Se,{type:"error",children:["Failed to create vessel: ",r.error.message]}),t.jsx(tp,{children:t.jsxs(rp,{onSubmit:y,children:[t.jsxs(Je,{children:[t.jsxs(Ye,{children:["Vessel Name",t.jsx(sp,{children:"*"})]}),t.jsx(st,{type:"text",value:n.name,onChange:j=>d("name",j.target.value),placeholder:"Enter vessel name (e.g., 'Sea Explorer', 'Fishing Buddy')",disabled:l,maxLength:100}),t.jsx(He,{children:"The primary name used to identify this vessel throughout the system."}),s.name&&t.jsx(Ze,{children:s.name})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Description"}),t.jsx(np,{value:n.description,onChange:j=>d("description",j.target.value),placeholder:"Optional description of the vessel (e.g., 'Center console fishing boat', '24ft cabin cruiser')",disabled:l,maxLength:500}),t.jsx(He,{children:"Optional description to help identify and categorize this vessel."}),s.description&&t.jsx(Ze,{children:s.description})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Hull Identification Number (HIN)"}),t.jsx(st,{type:"text",value:n.hullNumber,onChange:j=>d("hullNumber",j.target.value),placeholder:"Enter HIN if available",disabled:l,maxLength:50}),t.jsx(He,{children:"The unique hull identification number assigned by the manufacturer."}),s.hullNumber&&t.jsx(Ze,{children:s.hullNumber})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Manufacturer"}),t.jsx(st,{type:"text",value:n.manufacturer,onChange:j=>d("manufacturer",j.target.value),placeholder:"Enter manufacturer name",disabled:l,maxLength:100}),t.jsx(He,{children:"The company that built this vessel."}),s.manufacturer&&t.jsx(Ze,{children:s.manufacturer})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Model"}),t.jsx(st,{type:"text",value:n.model,onChange:j=>d("model",j.target.value),placeholder:"Enter model name",disabled:l,maxLength:100}),t.jsx(He,{children:"The specific model designation of this vessel."}),s.model&&t.jsx(Ze,{children:s.model})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Year Built"}),t.jsx(st,{type:"text",value:n.year,onChange:j=>d("year",j.target.value),placeholder:"Enter year (e.g., 2020)",disabled:l,maxLength:4}),t.jsx(He,{children:"The year this vessel was manufactured."}),s.year&&t.jsx(Ze,{children:s.year})]}),t.jsxs(Je,{children:[t.jsx(Ye,{children:"Length (feet)"}),t.jsx(st,{type:"text",value:n.length,onChange:j=>d("length",j.target.value),placeholder:"Enter length in feet (e.g., 24.5)",disabled:l}),t.jsx(He,{children:"The overall length of the vessel in feet."}),s.length&&t.jsx(Ze,{children:s.length})]}),t.jsxs(op,{children:[t.jsx(k,{type:"button",variant:"secondary",onClick:m,disabled:l,children:"CANCEL"}),t.jsx(Q,{children:t.jsx(k,{type:"submit",variant:"primary",disabled:l||!n.name.trim(),children:l?"CREATING VESSEL...":"CREATE VESSEL"})})]})]})})]})},Cr=a.div`
  padding: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,dp=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.lg};
`,pp=a(L)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,mp=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  align-items: end;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Bt=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,Sr=a.label`
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`,hp=a.select`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`,oo=a.input`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`,up=a.div`
  display: grid;
  gap: ${e=>e.theme.spacing.md};
`,gp=a(L)`
  cursor: pointer;
  transition: all ${e=>e.theme.animation.normal} ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: ${e=>e.theme.shadows.lg};
  }
`,xp=a.div`
  display: grid;
  grid-template-columns: 1fr auto;
  gap: ${e=>e.theme.spacing.md};
  align-items: start;
`,fp=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,yp=a.h3`
  margin: 0;
  font-size: ${e=>e.theme.typography.fontSize.lg};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: 1px;
`,bp=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: ${e=>e.theme.spacing.sm};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,jp=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
  text-align: right;
`,kr=a.div`
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
`,Tr=a.div`
  font-size: ${e=>e.theme.typography.fontSize.xs};
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: 1px;
`,vp=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.text.muted};
  
  .empty-title {
    font-size: ${e=>e.theme.typography.fontSize.xl};
    margin-bottom: ${e=>e.theme.spacing.md};
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }
  
  .empty-message {
    font-size: ${e=>e.theme.typography.fontSize.md};
    margin-bottom: ${e=>e.theme.spacing.lg};
  }
`,$p=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  text-transform: uppercase;
  letter-spacing: 2px;
`,wp=()=>{const e=de(),[r,n]=b.useState({}),{data:o,isLoading:s,error:i}=_e(r),{data:l}=ye(),c=(p,g)=>{n(u=>({...u,[p]:g||void 0}))},m=()=>{n({})},d=p=>{const g=Math.floor(p/3600),u=Math.floor(p%3600/60);return`${g}h ${u}m`},h=p=>`${(p*539957e-9).toFixed(1)} nm`,y=p=>`${p.toFixed(1)} kts`,j=p=>new Date(p).toLocaleDateString("en-US",{year:"numeric",month:"short",day:"numeric",hour:"2-digit",minute:"2-digit"}),f=p=>{const g=l==null?void 0:l.find(u=>u.id===p);return(g==null?void 0:g.name)||"Unknown Boat"};return s?t.jsx(Cr,{children:t.jsx($p,{children:"Loading Trip Data..."})}):i?t.jsx(Cr,{children:t.jsx(L,{variant:"accent",title:"System Error",children:t.jsxs("div",{style:{color:"red",textAlign:"center",padding:"2rem"},children:["Error loading trips: ",i.message]})})}):t.jsxs(Cr,{children:[t.jsxs(dp,{children:[t.jsx(q,{children:"Trip Log Database"}),t.jsx(k,{variant:"primary",onClick:()=>e("/trips/new"),children:"ADD MANUAL TRIP"})]}),t.jsx(pp,{title:"Search Parameters",variant:"secondary",children:t.jsxs(mp,{children:[t.jsxs(Bt,{children:[t.jsx(Sr,{children:"Vessel"}),t.jsxs(hp,{value:r.boatId||"",onChange:p=>c("boatId",p.target.value),children:[t.jsx("option",{value:"",children:"All Vessels"}),l==null?void 0:l.map(p=>t.jsx("option",{value:p.id,children:p.name},p.id))]})]}),t.jsxs(Bt,{children:[t.jsx(Sr,{children:"Start Date"}),t.jsx(oo,{type:"date",value:r.startDate||"",onChange:p=>c("startDate",p.target.value)})]}),t.jsxs(Bt,{children:[t.jsx(Sr,{children:"End Date"}),t.jsx(oo,{type:"date",value:r.endDate||"",onChange:p=>c("endDate",p.target.value)})]}),t.jsx(Bt,{children:t.jsx(k,{variant:"secondary",size:"sm",onClick:m,children:"Clear Filters"})})]})}),!o||o.length===0?t.jsxs(vp,{children:[t.jsx("div",{className:"empty-title",children:"No Trip Records Found"}),t.jsx("div",{className:"empty-message",children:Object.keys(r).length>0?"No trips match the current search parameters.":"No trips have been recorded yet."})]}):t.jsx(up,{children:o.map(p=>{var g,u,x,v,$,w;return t.jsx(oe,{to:`/trips/${p.id}`,style:{textDecoration:"none"},children:t.jsx(gp,{variant:"primary",children:t.jsxs(xp,{children:[t.jsxs(fp,{children:[t.jsxs(yp,{children:[f(p.boatId)," - ",j(p.startTime)]}),t.jsxs(bp,{children:[t.jsxs("div",{children:[t.jsx("strong",{children:"Water Type:"})," ",p.waterType.toUpperCase()]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Role:"})," ",p.role.toUpperCase()]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Duration:"})," ",d(((g=p.statistics)==null?void 0:g.durationSeconds)||0)]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Distance:"})," ",h(((u=p.statistics)==null?void 0:u.distanceMeters)||0)]})]})]}),t.jsxs(jp,{children:[t.jsxs("div",{children:[t.jsx(kr,{children:y(((x=p.statistics)==null?void 0:x.averageSpeedKnots)||0)}),t.jsx(Tr,{children:"Avg Speed"})]}),t.jsxs("div",{children:[t.jsx(kr,{children:y(((v=p.statistics)==null?void 0:v.maxSpeedKnots)||0)}),t.jsx(Tr,{children:"Max Speed"})]}),t.jsxs("div",{children:[t.jsx(kr,{children:((w=($=p.statistics)==null?void 0:$.stopPoints)==null?void 0:w.length)||0}),t.jsx(Tr,{children:"Stop Points"})]})]})]})})},p.id)})})]})},Ar=a.div`
  padding: ${e=>e.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`,Cp=a(k)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Sp=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${e=>e.theme.spacing.lg};
  margin-bottom: ${e=>e.theme.spacing.lg};
  
  @media (max-width: ${e=>e.theme.breakpoints.lg}) {
    grid-template-columns: 1fr;
  }
`,kp=a(L)`
  grid-column: 1 / -1;
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Tp=a(Qo)`
  height: 400px;
  width: 100%;
  border-radius: ${e=>e.theme.borderRadius.md};
  
  .leaflet-control-container {
    .leaflet-top.leaflet-left {
      .leaflet-control-zoom {
        background-color: ${e=>e.theme.colors.surface.dark};
        border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
        border-radius: ${e=>e.theme.borderRadius.sm};

        a {
          background-color: ${e=>e.theme.colors.surface.medium};
          color: ${e=>e.theme.colors.text.primary};
          border: none;

          &:hover {
            background-color: ${e=>e.theme.colors.primary.neonCarrot};
            color: ${e=>e.theme.colors.text.inverse};
          }
        }
      }
    }
  }
`,Ap=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
`,it=a.div`
  text-align: center;
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: ${e=>e.theme.spacing.md};
`,lt=a.div`
  font-size: ${e=>e.theme.typography.fontSize.xxl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  margin-bottom: ${e=>e.theme.spacing.xs};
`,ct=a.div`
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`,Fp=a.div`
  display: grid;
  gap: ${e=>e.theme.spacing.sm};
`,Xe=a.div`
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: ${e=>e.theme.spacing.md};
  padding: ${e=>e.theme.spacing.sm} 0;
  border-bottom: 1px solid ${e=>e.theme.colors.surface.light};
  
  &:last-child {
    border-bottom: none;
  }
`,et=a.div`
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,tt=a.div`
  font-size: ${e=>e.theme.typography.fontSize.md};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
`,Ep=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
`,vt=a.div`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.lilac};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: ${e=>e.theme.spacing.md};
  text-align: center;
`,$t=a.div`
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.lilac};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  margin-bottom: ${e=>e.theme.spacing.xs};
`,wt=a.div`
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`,Lp=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,zp=a.div`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: ${e=>e.theme.spacing.md};
`,Dp=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.sm};
`,Ip=a.div`
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.anakiwa};
  text-transform: uppercase;
  letter-spacing: 1px;
`,Mp=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  color: ${e=>e.theme.colors.text.secondary};
`,Rp=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.muted};
`,Np=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  text-transform: uppercase;
  letter-spacing: 2px;
`,Pp=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.status.error};
  font-size: ${e=>e.theme.typography.fontSize.lg};
`,Bp=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  margin-top: ${e=>e.theme.spacing.lg};
`,Op=a(L)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Up=a(L)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,qp=new Ie.Icon({iconUrl:"data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiM2NkZGNjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+",iconSize:[24,24],iconAnchor:[12,12]}),Hp=new Ie.Icon({iconUrl:"data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiNGRjY2NjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+",iconSize:[24,24],iconAnchor:[12,12]}),Wp=new Ie.Icon({iconUrl:"data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiNGRkZGNjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+",iconSize:[20,20],iconAnchor:[10,10]}),Vp=()=>{var x,v,$,w,A,T,I,D,U;const{id:e}=Ge(),{data:r,isLoading:n,error:o}=Sa(e),{data:s}=ye(),i=F=>{const R=Math.floor(F/3600),H=Math.floor(F%3600/60);return`${R}h ${H}m`},l=F=>`${(F*539957e-9).toFixed(1)} nm`,c=F=>`${F.toFixed(1)} kts`,m=F=>new Date(F).toLocaleString("en-US",{year:"numeric",month:"short",day:"numeric",hour:"2-digit",minute:"2-digit",second:"2-digit"}),d=(F,R)=>{const H=F>=0?"N":"S",G=R>=0?"E":"W";return`${Math.abs(F).toFixed(6)}°${H}, ${Math.abs(R).toFixed(6)}°${G}`},h=F=>{const R=s==null?void 0:s.find(H=>H.id===F);return(R==null?void 0:R.name)||"Unknown Boat"},y=F=>F.map(R=>[R.latitude,R.longitude]),j=F=>{if(F.length===0)return[0,0];const R=F.reduce((G,J)=>G+J.latitude,0)/F.length,H=F.reduce((G,J)=>G+J.longitude,0)/F.length;return[R,H]};if(n)return t.jsx(Ar,{children:t.jsx(Np,{children:"Loading Trip Data..."})});if(o||!r)return t.jsx(Ar,{children:t.jsx(Pp,{children:o?`Error loading trip: ${o.message}`:"Trip not found"})});const f=y(r.gpsPoints),p=j(r.gpsPoints),g=r.gpsPoints[0],u=r.gpsPoints[r.gpsPoints.length-1];return t.jsxs(Ar,{children:[t.jsx(Cp,{as:oe,to:"/trips",variant:"secondary",size:"sm",children:"← Back to Trip Log"}),t.jsxs(q,{children:["Trip Analysis - ",h(r.boatId)," - ",m(r.startTime)]}),f.length>0&&t.jsx(kp,{title:"Navigation Route",variant:"accent",children:t.jsxs(Tp,{center:p,zoom:13,scrollWheelZoom:!0,children:[t.jsx(an,{attribution:'© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',url:"https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"}),t.jsx(_o,{positions:f,color:"#FF9966",weight:3,opacity:.8}),g&&t.jsx($e,{position:[g.latitude,g.longitude],icon:qp,children:t.jsxs(we,{children:[t.jsx("strong",{children:"Trip Start"}),t.jsx("br",{}),m(r.startTime),t.jsx("br",{}),d(g.latitude,g.longitude)]})}),u&&t.jsx($e,{position:[u.latitude,u.longitude],icon:Hp,children:t.jsxs(we,{children:[t.jsx("strong",{children:"Trip End"}),t.jsx("br",{}),m(r.endTime),t.jsx("br",{}),d(u.latitude,u.longitude)]})}),(((x=r.statistics)==null?void 0:x.stopPoints)||[]).map((F,R)=>t.jsx($e,{position:[F.latitude,F.longitude],icon:Wp,children:t.jsxs(we,{children:[t.jsxs("strong",{children:["Stop Point ",R+1]}),t.jsx("br",{}),"Duration: ",i(F.durationSeconds),t.jsx("br",{}),d(F.latitude,F.longitude)]})},R))]})}),t.jsxs(Sp,{children:[t.jsx(L,{title:"Trip Statistics",variant:"primary",children:t.jsxs(Ap,{children:[t.jsxs(it,{children:[t.jsx(lt,{children:i(((v=r.statistics)==null?void 0:v.durationSeconds)||0)}),t.jsx(ct,{children:"Duration"})]}),t.jsxs(it,{children:[t.jsx(lt,{children:l((($=r.statistics)==null?void 0:$.distanceMeters)||0)}),t.jsx(ct,{children:"Distance"})]}),t.jsxs(it,{children:[t.jsx(lt,{children:c(((w=r.statistics)==null?void 0:w.averageSpeedKnots)||0)}),t.jsx(ct,{children:"Avg Speed"})]}),t.jsxs(it,{children:[t.jsx(lt,{children:c(((A=r.statistics)==null?void 0:A.maxSpeedKnots)||0)}),t.jsx(ct,{children:"Max Speed"})]}),t.jsxs(it,{children:[t.jsx(lt,{children:((I=(T=r.statistics)==null?void 0:T.stopPoints)==null?void 0:I.length)||0}),t.jsx(ct,{children:"Stop Points"})]}),t.jsxs(it,{children:[t.jsx(lt,{children:r.gpsPoints.length}),t.jsx(ct,{children:"GPS Points"})]})]})}),t.jsx(L,{title:"Trip Information",variant:"secondary",children:t.jsxs(Fp,{children:[t.jsxs(Xe,{children:[t.jsx(et,{children:"Vessel"}),t.jsx(tt,{children:h(r.boatId)})]}),t.jsxs(Xe,{children:[t.jsx(et,{children:"Start Time"}),t.jsx(tt,{children:m(r.startTime)})]}),t.jsxs(Xe,{children:[t.jsx(et,{children:"End Time"}),t.jsx(tt,{children:m(r.endTime)})]}),t.jsxs(Xe,{children:[t.jsx(et,{children:"Water Type"}),t.jsx(tt,{children:r.waterType.toUpperCase()})]}),t.jsxs(Xe,{children:[t.jsx(et,{children:"Role"}),t.jsx(tt,{children:r.role.toUpperCase()})]}),g&&t.jsxs(Xe,{children:[t.jsx(et,{children:"Start Position"}),t.jsx(tt,{children:d(g.latitude,g.longitude)})]}),u&&t.jsxs(Xe,{children:[t.jsx(et,{children:"End Position"}),t.jsx(tt,{children:d(u.latitude,u.longitude)})]})]})})]}),r.manualData&&t.jsx(Op,{title:"Manual Data Entry",variant:"accent",children:t.jsxs(Ep,{children:[r.manualData.engineHours!==void 0&&t.jsxs(vt,{children:[t.jsx($t,{children:r.manualData.engineHours}),t.jsx(wt,{children:"Engine Hours"})]}),r.manualData.fuelConsumed!==void 0&&t.jsxs(vt,{children:[t.jsx($t,{children:r.manualData.fuelConsumed}),t.jsx(wt,{children:"Fuel Consumed"})]}),r.manualData.numberOfPassengers!==void 0&&t.jsxs(vt,{children:[t.jsx($t,{children:r.manualData.numberOfPassengers}),t.jsx(wt,{children:"Passengers"})]}),r.manualData.weatherConditions&&t.jsxs(vt,{children:[t.jsx($t,{children:r.manualData.weatherConditions}),t.jsx(wt,{children:"Weather"})]}),r.manualData.destination&&t.jsxs(vt,{children:[t.jsx($t,{children:r.manualData.destination}),t.jsx(wt,{children:"Destination"})]})]})}),(((D=r.statistics)==null?void 0:D.stopPoints)||[]).length>0&&t.jsx(Up,{title:"Stop Points Analysis",variant:"primary",children:t.jsx(Lp,{children:(((U=r.statistics)==null?void 0:U.stopPoints)||[]).map((F,R)=>t.jsxs(zp,{children:[t.jsxs(Dp,{children:[t.jsxs(Ip,{children:["Stop Point ",R+1]}),t.jsx(Mp,{children:i(F.durationSeconds)})]}),t.jsx(Rp,{children:d(F.latitude,F.longitude)}),t.jsxs("div",{style:{fontSize:"0.8rem",color:"#999",marginTop:"0.5rem"},children:[m(F.startTime)," - ",m(F.endTime)]})]},R))})}),t.jsxs(Bp,{children:[t.jsx(Q,{children:t.jsx(oe,{to:`/trips/${r.id}/edit`,style:{textDecoration:"none"},children:t.jsx(k,{variant:"primary",children:"Edit Trip Data"})})}),t.jsx(k,{variant:"secondary",children:"Export Data"})]})]})},Fr=a.div`
  padding: ${e=>e.theme.spacing.lg};
  max-width: 1000px;
  margin: 0 auto;
`,Kp=a(k)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Gp=a.div`
  display: grid;
  gap: ${e=>e.theme.spacing.lg};
`,ao=a(L)`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Ct=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.md};
  
  &:last-child {
    margin-bottom: 0;
  }
`,Ee=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,Le=a.label`
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,dt=a.input`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }

  &:disabled {
    background-color: ${e=>e.theme.colors.surface.dark};
    color: ${e=>e.theme.colors.text.muted};
    cursor: not-allowed;
  }
`,Er=a.select`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`,_p=a.textarea`
  background-color: ${e=>e.theme.colors.surface.medium};
  border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  resize: vertical;
  min-height: 100px;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`,Lr=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${e=>e.theme.spacing.lg};
`,Qp=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  text-transform: uppercase;
  letter-spacing: 2px;
`,Jp=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.status.error};
  font-size: ${e=>e.theme.typography.fontSize.lg};
`,Yp=a.div`
  background-color: rgba(102, 255, 102, 0.1);
  border: 1px solid ${e=>e.theme.colors.status.success};
  border-radius: ${e=>e.theme.borderRadius.md};
  color: ${e=>e.theme.colors.status.success};
  padding: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
  text-align: center;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
`,Zp=()=>{const{id:e}=Ge(),{data:r,isLoading:n,error:o}=Sa(e),{data:s}=ye(),i=yc(),l=bc(),[c,m]=b.useState({waterType:"inland",role:"captain",boatId:""}),[d,h]=b.useState({}),[y,j]=b.useState("");b.useEffect(()=>{r&&(m({waterType:r.waterType,role:r.role,boatId:r.boatId}),r.manualData&&h({engineHours:r.manualData.engineHours??void 0,fuelConsumed:r.manualData.fuelConsumed??void 0,weatherConditions:r.manualData.weatherConditions??void 0,numberOfPassengers:r.manualData.numberOfPassengers??void 0,destination:r.manualData.destination??void 0}))},[r]);const f=($,w)=>{m(A=>({...A,[$]:w}))},p=($,w)=>{h(A=>({...A,[$]:w===""?void 0:w}))},g=async()=>{if(r)try{await i.mutateAsync({id:r.id,data:c}),j("Trip information updated successfully!"),setTimeout(()=>j(""),3e3)}catch($){console.error("Error updating trip:",$)}},u=async()=>{if(!r)return;const $={};Object.entries(d).forEach(([w,A])=>{A!=null&&A!==""&&!(typeof A=="number"&&isNaN(A))&&($[w]=A)});try{await l.mutateAsync({tripId:r.id,data:$}),j("Manual data updated successfully!"),setTimeout(()=>j(""),3e3)}catch(w){console.error("Error updating manual data:",w)}},x=$=>new Date($).toLocaleString("en-US",{year:"numeric",month:"short",day:"numeric",hour:"2-digit",minute:"2-digit"}),v=$=>{const w=s==null?void 0:s.find(A=>A.id===$);return(w==null?void 0:w.name)||"Unknown Boat"};return n?t.jsx(Fr,{children:t.jsx(Qp,{children:"Loading Trip Data..."})}):o||!r?t.jsx(Fr,{children:t.jsx(Jp,{children:o?`Error loading trip: ${o.message}`:"Trip not found"})}):t.jsxs(Fr,{children:[t.jsx(Kp,{as:oe,to:`/trips/${r.id}`,variant:"secondary",size:"sm",children:"← Back to Trip Details"}),t.jsxs(q,{children:["Edit Trip Data - ",v(r.boatId)," - ",x(r.startTime)]}),y&&t.jsx(Yp,{children:y}),t.jsxs(Gp,{children:[t.jsxs(ao,{title:"Trip Information",variant:"primary",children:[t.jsxs(Ct,{children:[t.jsxs(Ee,{children:[t.jsx(Le,{children:"Vessel"}),t.jsx(Er,{value:c.boatId,onChange:$=>f("boatId",$.target.value),children:s==null?void 0:s.map($=>t.jsx("option",{value:$.id,children:$.name},$.id))})]}),t.jsxs(Ee,{children:[t.jsx(Le,{children:"Water Type"}),t.jsxs(Er,{value:c.waterType,onChange:$=>f("waterType",$.target.value),children:[t.jsx("option",{value:"inland",children:"Inland"}),t.jsx("option",{value:"coastal",children:"Coastal/Nearshore"}),t.jsx("option",{value:"offshore",children:"Offshore"})]})]}),t.jsxs(Ee,{children:[t.jsx(Le,{children:"Role"}),t.jsxs(Er,{value:c.role,onChange:$=>f("role",$.target.value),children:[t.jsx("option",{value:"captain",children:"Captain"}),t.jsx("option",{value:"crew",children:"Crew"}),t.jsx("option",{value:"observer",children:"Observer"})]})]})]}),t.jsxs(Ct,{children:[t.jsxs(Ee,{children:[t.jsx(Le,{children:"Start Time"}),t.jsx(dt,{type:"text",value:x(r.startTime),disabled:!0})]}),t.jsxs(Ee,{children:[t.jsx(Le,{children:"End Time"}),t.jsx(dt,{type:"text",value:x(r.endTime),disabled:!0})]})]}),t.jsx(Lr,{children:t.jsx(Q,{children:t.jsx(k,{variant:"primary",onClick:g,disabled:i.isPending,children:i.isPending?"Saving...":"Save Trip Information"})})})]}),t.jsxs(ao,{title:"Manual Data Entry",variant:"secondary",children:[t.jsxs(Ct,{children:[t.jsxs(Ee,{children:[t.jsx(Le,{children:"Engine Hours"}),t.jsx(dt,{type:"number",step:"0.1",min:"0",placeholder:"0.0",value:d.engineHours||"",onChange:$=>p("engineHours",parseFloat($.target.value))})]}),t.jsxs(Ee,{children:[t.jsx(Le,{children:"Fuel Consumed (gallons)"}),t.jsx(dt,{type:"number",step:"0.1",min:"0",placeholder:"0.0",value:d.fuelConsumed||"",onChange:$=>p("fuelConsumed",parseFloat($.target.value))})]}),t.jsxs(Ee,{children:[t.jsx(Le,{children:"Number of Passengers"}),t.jsx(dt,{type:"number",min:"0",placeholder:"0",value:d.numberOfPassengers||"",onChange:$=>p("numberOfPassengers",parseInt($.target.value))})]})]}),t.jsx(Ct,{children:t.jsxs(Ee,{children:[t.jsx(Le,{children:"Destination"}),t.jsx(dt,{type:"text",placeholder:"Enter destination",value:d.destination||"",onChange:$=>p("destination",$.target.value)})]})}),t.jsx(Ct,{children:t.jsxs(Ee,{children:[t.jsx(Le,{children:"Weather Conditions"}),t.jsx(_p,{placeholder:"Describe weather conditions, sea state, visibility, etc.",value:d.weatherConditions||"",onChange:$=>p("weatherConditions",$.target.value)})]})}),t.jsx(Lr,{children:t.jsx(Q,{children:t.jsx(k,{variant:"secondary",onClick:u,disabled:l.isPending,children:l.isPending?"Saving...":"Save Manual Data"})})})]})]}),t.jsxs(Lr,{children:[t.jsx(oe,{to:`/trips/${r.id}`,style:{textDecoration:"none"},children:t.jsx(k,{variant:"accent",children:"View Trip Details"})}),t.jsx(oe,{to:"/trips",style:{textDecoration:"none"},children:t.jsx(k,{variant:"secondary",children:"Back to Trip Log"})})]})]})},Xp=a.div`
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
`,em=a(L)`
  padding: 30px;
  margin-top: 20px;
`,tm=a.form`
  display: flex;
  flex-direction: column;
  gap: 25px;
`,St=a.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`,so=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
`,kt=a.label`
  color: ${e=>e.theme.colors.text.primary};
  font-size: 1rem;
  text-transform: uppercase;
  font-weight: bold;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
`,io=a.input`
  padding: 15px 20px;
  background: ${e=>e.theme.colors.background};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  /* Style the calendar picker */
  &::-webkit-calendar-picker-indicator {
    filter: invert(1);
    cursor: pointer;
    padding: 5px;
  }
`,zr=a.select`
  padding: 15px 20px;
  background: ${e=>e.theme.colors.background};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${e=>e.theme.colors.background};
    color: ${e=>e.theme.colors.text.primary};
  }
`,Tt=a.p`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.4;
`,rm=a.div`
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid ${e=>e.theme.colors.primary.anakiwa};
`,nm=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`,Dr=a.span`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin-left: 5px;
`,Ir=a.div`
  color: ${e=>e.theme.colors.status.error};
  font-size: 0.9rem;
  margin-top: 5px;
`,om=()=>{const e=de(),{data:r,isLoading:n}=ye(),o=fc(),[s,i]=b.useState({boatId:"",startTime:"",endTime:"",waterType:"inland",role:"captain"}),[l,c]=b.useState({}),[m,d]=b.useState(!1),h=()=>{e("/trips")},y=(g,u)=>{i(x=>({...x,[g]:u})),l[g]&&c(x=>({...x,[g]:void 0}))},j=()=>{const g={};if(s.boatId||(g.boatId="Please select a vessel"),s.startTime||(g.startTime="Start time is required"),s.endTime||(g.endTime="End time is required"),s.startTime&&s.endTime){const u=new Date(s.startTime);new Date(s.endTime)<=u&&(g.endTime="End time must be after start time")}return c(g),Object.keys(g).length===0},f=async g=>{if(g.preventDefault(),!!j()){d(!0);try{const u=await o.mutateAsync({boatId:s.boatId,startTime:new Date(s.startTime).toISOString(),endTime:new Date(s.endTime).toISOString(),waterType:s.waterType,role:s.role,gpsPoints:[]});e(`/trips/${u.id}`)}catch(u){console.error("Failed to create trip:",u)}finally{d(!1)}}},p=(r==null?void 0:r.filter(g=>g.enabled))||[];return t.jsxs(Xp,{children:[t.jsxs(nm,{children:[t.jsx(q,{children:"ADD MANUAL TRIP"}),t.jsx(k,{variant:"secondary",onClick:h,children:"BACK TO TRIPS"})]}),o.error&&t.jsxs(Se,{type:"error",children:["Failed to create trip: ",o.error.message]}),t.jsx(em,{children:t.jsxs(tm,{onSubmit:f,children:[t.jsxs(St,{children:[t.jsxs(kt,{children:["Vessel",t.jsx(Dr,{children:"*"})]}),t.jsxs(zr,{value:s.boatId,onChange:g=>y("boatId",g.target.value),disabled:m||n,children:[t.jsx("option",{value:"",children:"-- Select Vessel --"}),p.map(g=>t.jsx("option",{value:g.id,children:g.name},g.id))]}),t.jsx(Tt,{children:"Select the vessel used for this trip."}),l.boatId&&t.jsx(Ir,{children:l.boatId})]}),t.jsxs(so,{children:[t.jsxs(St,{children:[t.jsxs(kt,{children:["Start Date & Time",t.jsx(Dr,{children:"*"})]}),t.jsx(io,{type:"datetime-local",value:s.startTime,onChange:g=>y("startTime",g.target.value),disabled:m}),t.jsx(Tt,{children:"When did the trip begin?"}),l.startTime&&t.jsx(Ir,{children:l.startTime})]}),t.jsxs(St,{children:[t.jsxs(kt,{children:["End Date & Time",t.jsx(Dr,{children:"*"})]}),t.jsx(io,{type:"datetime-local",value:s.endTime,onChange:g=>y("endTime",g.target.value),disabled:m}),t.jsx(Tt,{children:"When did the trip end?"}),l.endTime&&t.jsx(Ir,{children:l.endTime})]})]}),t.jsxs(so,{children:[t.jsxs(St,{children:[t.jsx(kt,{children:"Water Type"}),t.jsxs(zr,{value:s.waterType,onChange:g=>y("waterType",g.target.value),disabled:m,children:[t.jsx("option",{value:"inland",children:"Inland"}),t.jsx("option",{value:"coastal",children:"Coastal / Nearshore"}),t.jsx("option",{value:"offshore",children:"Offshore"})]}),t.jsx(Tt,{children:"The type of waters navigated during this trip."})]}),t.jsxs(St,{children:[t.jsx(kt,{children:"Your Role"}),t.jsxs(zr,{value:s.role,onChange:g=>y("role",g.target.value),disabled:m,children:[t.jsx("option",{value:"captain",children:"Captain"}),t.jsx("option",{value:"crew",children:"Crew"}),t.jsx("option",{value:"observer",children:"Observer"})]}),t.jsx(Tt,{children:"Your role during this trip."})]})]}),t.jsxs(rm,{children:[t.jsx(k,{type:"button",variant:"secondary",onClick:h,disabled:m,children:"CANCEL"}),t.jsx(Q,{children:t.jsx(k,{type:"submit",variant:"primary",disabled:m||!s.boatId,children:m?"CREATING TRIP...":"CREATE TRIP"})})]})]})})]})},za=e=>he({queryKey:["notes",e],queryFn:()=>O.getNotes(e)}),Da=e=>he({queryKey:["notes",e],queryFn:()=>O.getNote(e),enabled:!!e}),am=()=>{const e=Y();return ee({mutationFn:r=>O.createNote(r),onSuccess:()=>{e.invalidateQueries({queryKey:["notes"]})}})},sm=()=>{const e=Y();return ee({mutationFn:({id:r,data:n})=>O.updateNote(r,n),onSuccess:r=>{e.invalidateQueries({queryKey:["notes"]}),e.setQueryData(["notes",r.id],r)}})},Ia=()=>{const e=Y();return ee({mutationFn:r=>O.deleteNote(r),onSuccess:()=>{e.invalidateQueries({queryKey:["notes"]})}})},Ma=()=>{const{data:e}=za();return((e==null?void 0:e.reduce((n,o)=>(o.tags.forEach(s=>{n.includes(s)||n.push(s)}),n),[]))||[]).sort()},lo=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
`,im=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
`,lm=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Ot=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,Ut=a.label`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,Mr=a.select`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  option {
    background-color: ${e=>e.theme.colors.surface.dark};
    color: ${e=>e.theme.colors.text.primary};
  }
`,cm=a.input`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.muted};
  }
`,dm=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: ${e=>e.theme.spacing.md};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,pm=a.div`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.lilac};
  border-radius: ${e=>e.theme.borderRadius.lg};
  padding: ${e=>e.theme.spacing.md};
  cursor: pointer;
  transition: all ${e=>e.theme.animation.normal} ease;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: ${e=>e.theme.shadows.glow};
  }
`,mm=a.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: ${e=>e.theme.spacing.sm};
`,hm=a.span`
  background-color: ${e=>{switch(e.type){case"boat":return e.theme.colors.primary.anakiwa;case"trip":return e.theme.colors.primary.lilac;default:return e.theme.colors.primary.neonCarrot}}};
  color: ${e=>e.theme.colors.text.inverse};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
`,um=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.xs};
`,co=a.button`
  background: none;
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.primary.anakiwa};
  padding: ${e=>e.theme.spacing.xs};
  border-radius: ${e=>e.theme.borderRadius.sm};
  cursor: pointer;
  font-size: ${e=>e.theme.typography.fontSize.xs};
  transition: all ${e=>e.theme.animation.fast} ease;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }

  &.danger:hover {
    border-color: ${e=>e.theme.colors.status.error};
    color: ${e=>e.theme.colors.status.error};
  }
`,gm=a.div`
  color: ${e=>e.theme.colors.text.primary};
  line-height: ${e=>e.theme.typography.lineHeight.normal};
  margin-bottom: ${e=>e.theme.spacing.sm};
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
`,xm=a.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${e=>e.theme.spacing.xs};
  margin-bottom: ${e=>e.theme.spacing.sm};
`,fm=a.span`
  background-color: ${e=>e.theme.colors.surface.medium};
  color: ${e=>e.theme.colors.text.secondary};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
`,ym=a.div`
  color: ${e=>e.theme.colors.text.muted};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  text-align: right;
`,bm=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.text.muted};
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: ${e=>e.theme.spacing.md};
  }
  
  .empty-title {
    font-size: ${e=>e.theme.typography.fontSize.lg};
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    margin-bottom: ${e=>e.theme.spacing.sm};
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }
`,jm=()=>{const e=de(),[r,n]=b.useState(""),[o,s]=b.useState(""),[i,l]=b.useState(""),[c,m]=b.useState(""),{data:d}=ye(),h=Ma(),y=b.useMemo(()=>{const T={};return r&&(T.type=r),o&&(T.boatId=o),i&&(T.tags=[i]),T},[r,o,i]),{data:j,isLoading:f}=za(y),p=Ia(),g=b.useMemo(()=>j?j.filter(T=>{if(c){const I=c.toLowerCase();return T.content.toLowerCase().includes(I)||T.tags.some(D=>D.toLowerCase().includes(I))}return!0}):[],[j,c]),u=()=>{e("/notes/new")},x=(T,I)=>{I.stopPropagation(),e(`/notes/${T}/edit`)},v=async(T,I)=>{if(I.stopPropagation(),window.confirm("Are you sure you want to delete this note?"))try{await p.mutateAsync(T)}catch(D){console.error("Failed to delete note:",D)}},$=T=>{e(`/notes/${T}`)},w=T=>new Date(T).toLocaleDateString("en-US",{year:"numeric",month:"short",day:"numeric",hour:"2-digit",minute:"2-digit"}),A=T=>{if(!T||!d)return null;const I=d.find(D=>D.id===T);return I==null?void 0:I.name};return f?t.jsxs(lo,{children:[t.jsx(q,{level:1,children:"Notes Database"}),t.jsx(L,{title:"Loading",children:t.jsx("div",{style:{textAlign:"center",padding:"2rem"},children:"Loading notes..."})})]}):t.jsxs(lo,{children:[t.jsxs(im,{children:[t.jsx(q,{level:1,children:"Notes Database"}),t.jsx(Q,{children:t.jsx(k,{onClick:u,children:"Create New Note"})})]}),t.jsx(L,{title:"Filters",variant:"secondary",children:t.jsxs(lm,{children:[t.jsxs(Ot,{children:[t.jsx(Ut,{children:"Note Type"}),t.jsxs(Mr,{value:r,onChange:T=>n(T.target.value),children:[t.jsx("option",{value:"",children:"All Types"}),t.jsx("option",{value:"general",children:"General"}),t.jsx("option",{value:"boat",children:"Boat-Specific"}),t.jsx("option",{value:"trip",children:"Trip"})]})]}),t.jsxs(Ot,{children:[t.jsx(Ut,{children:"Boat"}),t.jsxs(Mr,{value:o,onChange:T=>s(T.target.value),disabled:r==="general"||r==="trip",children:[t.jsx("option",{value:"",children:"All Boats"}),d==null?void 0:d.map(T=>t.jsx("option",{value:T.id,children:T.name},T.id))]})]}),t.jsxs(Ot,{children:[t.jsx(Ut,{children:"Tag"}),t.jsxs(Mr,{value:i,onChange:T=>l(T.target.value),children:[t.jsx("option",{value:"",children:"All Tags"}),h.map(T=>t.jsx("option",{value:T,children:T},T))]})]}),t.jsxs(Ot,{children:[t.jsx(Ut,{children:"Search"}),t.jsx(cm,{type:"text",placeholder:"Search notes content...",value:c,onChange:T=>m(T.target.value)})]})]})}),g.length===0?t.jsx(L,{children:t.jsxs(bm,{children:[t.jsx("div",{className:"empty-icon",children:"📝"}),t.jsx("div",{className:"empty-title",children:"No Notes Found"}),t.jsx("div",{children:(j==null?void 0:j.length)===0?"Create your first note to get started.":"Try adjusting your filters to find notes."})]})}):t.jsx(dm,{children:g.map(T=>t.jsxs(pm,{onClick:()=>$(T.id),children:[t.jsxs(mm,{children:[t.jsxs(hm,{type:T.type,children:[T.type,T.type==="boat"&&A(T.boatId)&&` - ${A(T.boatId)}`]}),t.jsxs(um,{children:[t.jsx(Q,{children:t.jsx(co,{onClick:I=>x(T.id,I),children:"Edit"})}),t.jsx(Q,{children:t.jsx(co,{className:"danger",onClick:I=>v(T.id,I),children:"Delete"})})]})]}),t.jsx(gm,{children:T.content}),T.tags.length>0&&t.jsx(xm,{children:T.tags.map(I=>t.jsx(fm,{children:I},I))}),t.jsxs(ym,{children:[w(T.createdAt),T.updatedAt!==T.createdAt&&" (edited)"]})]},T.id))})]})},Rr=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 800px;
  margin: 0 auto;
`,vm=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
`,$m=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
`,wm=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,pt=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,mt=a.span`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,ht=a.span`
  color: ${e=>e.theme.colors.text.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
`,Cm=a.span`
  background-color: ${e=>{switch(e.type){case"boat":return e.theme.colors.primary.anakiwa;case"trip":return e.theme.colors.primary.lilac;default:return e.theme.colors.primary.neonCarrot}}};
  color: ${e=>e.theme.colors.text.inverse};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  display: inline-block;
`,Sm=a.div`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: ${e=>e.theme.spacing.lg};
  color: ${e=>e.theme.colors.text.primary};
  line-height: ${e=>e.theme.typography.lineHeight.normal};
  white-space: pre-wrap;
  font-size: ${e=>e.theme.typography.fontSize.md};
`,km=a.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${e=>e.theme.spacing.sm};
`,Tm=a.span`
  background-color: ${e=>e.theme.colors.primary.lilac};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.sm};
`,Am=a.span`
  color: ${e=>e.theme.colors.text.muted};
  font-style: italic;
`,Fm=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.text.muted};
`,Em=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xxl};
  color: ${e=>e.theme.colors.status.error};
`,Lm=()=>{const e=de(),{id:r}=Ge(),{data:n,isLoading:o,error:s}=Da(r||""),{data:i}=ye(),{data:l}=_e(),c=Ia(),m=()=>{e(`/notes/${r}/edit`)},d=async()=>{if(window.confirm("Are you sure you want to delete this note?"))try{await c.mutateAsync(r),e("/notes")}catch(p){console.error("Failed to delete note:",p)}},h=()=>{e("/notes")},y=p=>new Date(p).toLocaleDateString("en-US",{year:"numeric",month:"long",day:"numeric",hour:"2-digit",minute:"2-digit"}),j=p=>{if(!p||!i)return"Unknown Boat";const g=i.find(u=>u.id===p);return(g==null?void 0:g.name)||"Unknown Boat"},f=p=>{if(!p||!l)return"Unknown Trip";const g=l.find(v=>v.id===p);if(!g)return"Unknown Trip";const u=j(g.boatId);return`${new Date(g.startTime).toLocaleDateString()} - ${u}`};return o?t.jsxs(Rr,{children:[t.jsx(q,{level:1,children:"Note Details"}),t.jsx(L,{children:t.jsx(Fm,{children:"Loading note..."})})]}):s||!n?t.jsxs(Rr,{children:[t.jsx(q,{level:1,children:"Note Details"}),t.jsx(L,{children:t.jsxs(Em,{children:["Note not found or failed to load.",t.jsx("div",{style:{marginTop:"1rem"},children:t.jsx(k,{onClick:h,children:"Back to Notes"})})]})})]}):t.jsxs(Rr,{children:[t.jsxs(vm,{children:[t.jsx(q,{level:1,children:"Note Details"}),t.jsxs($m,{children:[t.jsx(k,{variant:"secondary",onClick:h,children:"Back to Notes"}),t.jsx(Q,{children:t.jsx(k,{variant:"accent",onClick:m,children:"Edit Note"})}),t.jsx(Q,{children:t.jsx(k,{variant:"danger",onClick:d,disabled:c.isPending,children:c.isPending?"Deleting...":"Delete"})})]})]}),t.jsx(L,{title:"Note Information",children:t.jsxs(wm,{children:[t.jsxs(pt,{children:[t.jsx(mt,{children:"Type"}),t.jsx(ht,{children:t.jsx(Cm,{type:n.type,children:n.type})})]}),n.type==="boat"&&n.boatId&&t.jsxs(pt,{children:[t.jsx(mt,{children:"Boat"}),t.jsx(ht,{children:j(n.boatId)})]}),n.type==="trip"&&n.tripId&&t.jsxs(pt,{children:[t.jsx(mt,{children:"Trip"}),t.jsx(ht,{children:f(n.tripId)})]}),t.jsxs(pt,{children:[t.jsx(mt,{children:"Created"}),t.jsx(ht,{children:y(n.createdAt)})]}),n.updatedAt!==n.createdAt&&t.jsxs(pt,{children:[t.jsx(mt,{children:"Last Modified"}),t.jsx(ht,{children:y(n.updatedAt)})]}),t.jsxs(pt,{children:[t.jsx(mt,{children:"Tags"}),t.jsx(ht,{children:n.tags.length>0?t.jsx(km,{children:n.tags.map(p=>t.jsx(Tm,{children:p},p))}):t.jsx(Am,{children:"No tags"})})]})]})}),t.jsx(L,{title:"Content",children:t.jsx(Sm,{children:n.content})})]})},po=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 800px;
  margin: 0 auto;
`,zm=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
`,Dm=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
`,qt=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,ut=a.label`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,Nr=a.select`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  option {
    background-color: ${e=>e.theme.colors.surface.dark};
    color: ${e=>e.theme.colors.text.primary};
  }
`,Im=a.textarea`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.md};
  border-radius: ${e=>e.theme.borderRadius.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  line-height: ${e=>e.theme.typography.lineHeight.normal};
  min-height: 200px;
  resize: vertical;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.muted};
  }
`,Mm=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,Rm=a.input`
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.muted};
  }
`,Nm=a.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${e=>e.theme.spacing.sm};
`,Pm=a.span`
  background-color: ${e=>e.theme.colors.primary.lilac};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.xs};

  .remove-tag {
    background: none;
    border: none;
    color: ${e=>e.theme.colors.text.primary};
    cursor: pointer;
    font-size: ${e=>e.theme.typography.fontSize.sm};
    padding: 0;

    &:hover {
      color: ${e=>e.theme.colors.status.error};
    }
  }
`,Bm=a.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${e=>e.theme.spacing.xs};
  margin-top: ${e=>e.theme.spacing.sm};
`,Om=a.button`
  background: none;
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.primary.anakiwa};
  padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
  border-radius: ${e=>e.theme.borderRadius.pill};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  cursor: pointer;
  transition: all ${e=>e.theme.animation.fast} ease;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.primary.neonCarrot};
    background-color: ${e=>e.theme.colors.primary.neonCarrot}20;
  }
`,Um=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${e=>e.theme.spacing.lg};
`,qm=a.div`
  color: ${e=>e.theme.colors.status.error};
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.status.error};
  padding: ${e=>e.theme.spacing.md};
  border-radius: ${e=>e.theme.borderRadius.md};
  margin-bottom: ${e=>e.theme.spacing.md};
`,mo=()=>{const e=de(),{id:r}=Ge(),n=!!r&&r!=="new",[o,s]=b.useState("general"),[i,l]=b.useState(""),[c,m]=b.useState(""),[d,h]=b.useState(""),[y,j]=b.useState([]),[f,p]=b.useState(""),[g,u]=b.useState(""),{data:x,isLoading:v}=Da(r||""),{data:$}=ye(),{data:w}=_e(),A=Ma(),T=am(),I=sm();b.useEffect(()=>{x&&n&&(s(x.type),l(x.boatId||""),m(x.tripId||""),h(x.content),j(x.tags))},[x,n]);const D=()=>{const B=f.trim();B&&!y.includes(B)&&(j([...y,B]),p(""))},U=B=>{j(y.filter(te=>te!==B))},F=B=>{y.includes(B)||j([...y,B])},R=B=>{B.key==="Enter"&&(B.preventDefault(),D())},H=async()=>{if(u(""),!d.trim()){u("Note content is required");return}if(o==="boat"&&!i){u("Please select a boat for boat-specific notes");return}if(o==="trip"&&!c){u("Please select a trip for trip notes");return}try{const B={content:d.trim(),type:o,boatId:o==="boat"?i:void 0,tripId:o==="trip"?c:void 0,tags:y};n?await I.mutateAsync({id:r,data:B}):await T.mutateAsync(B),e("/notes")}catch(B){console.error("Failed to save note:",B),u("Failed to save note. Please try again.")}},G=()=>{e("/notes")},J=A.filter(B=>!y.includes(B));return v&&n?t.jsxs(po,{children:[t.jsx(q,{level:1,children:"Loading Note"}),t.jsx(L,{title:"Loading",children:t.jsx("div",{style:{textAlign:"center",padding:"2rem"},children:"Loading note data..."})})]}):t.jsxs(po,{children:[t.jsx(zm,{children:t.jsx(q,{level:1,children:n?"Edit Note":"Create New Note"})}),t.jsx(L,{title:"Note Details",children:t.jsxs(Dm,{children:[g&&t.jsx(qm,{children:g}),t.jsxs(qt,{children:[t.jsx(ut,{children:"Note Type"}),t.jsxs(Nr,{value:o,onChange:B=>{s(B.target.value),l(""),m("")},children:[t.jsx("option",{value:"general",children:"General Note"}),t.jsx("option",{value:"boat",children:"Boat-Specific Note"}),t.jsx("option",{value:"trip",children:"Trip Note"})]})]}),o==="boat"&&t.jsxs(qt,{children:[t.jsx(ut,{children:"Boat"}),t.jsxs(Nr,{value:i,onChange:B=>l(B.target.value),children:[t.jsx("option",{value:"",children:"Select a boat"}),$==null?void 0:$.map(B=>t.jsx("option",{value:B.id,children:B.name},B.id))]})]}),o==="trip"&&t.jsxs(qt,{children:[t.jsx(ut,{children:"Trip"}),t.jsxs(Nr,{value:c,onChange:B=>m(B.target.value),children:[t.jsx("option",{value:"",children:"Select a trip"}),w==null?void 0:w.map(B=>{var te;return t.jsxs("option",{value:B.id,children:[new Date(B.startTime).toLocaleDateString()," - ",((te=$==null?void 0:$.find(be=>be.id===B.boatId))==null?void 0:te.name)||"Unknown Boat"]},B.id)})]})]}),t.jsxs(qt,{children:[t.jsx(ut,{children:"Content"}),t.jsx(Im,{value:d,onChange:B=>h(B.target.value),placeholder:"Enter your note content here..."})]}),t.jsxs(Mm,{children:[t.jsx(ut,{children:"Tags"}),t.jsx(Rm,{type:"text",value:f,onChange:B=>p(B.target.value),onKeyPress:R,placeholder:"Add a tag and press Enter"}),y.length>0&&t.jsx(Nm,{children:y.map(B=>t.jsxs(Pm,{children:[B,t.jsx("button",{className:"remove-tag",onClick:()=>U(B),type:"button",children:"×"})]},B))}),J.length>0&&t.jsxs("div",{children:[t.jsx(ut,{style:{fontSize:"12px",marginBottom:"8px"},children:"Suggested Tags"}),t.jsx(Bm,{children:J.slice(0,10).map(B=>t.jsx(Om,{onClick:()=>F(B),type:"button",children:B},B))})]})]}),t.jsxs(Um,{children:[t.jsx(k,{variant:"secondary",onClick:G,children:"Cancel"}),t.jsx(Q,{children:t.jsx(k,{onClick:H,disabled:T.isPending||I.isPending,children:T.isPending||I.isPending?"Saving...":"Save Note"})})]})]})})]})},X={todoLists:e=>["todoLists",e],todoList:e=>["todoList",e]},Hm=e=>he({queryKey:X.todoLists(e),queryFn:()=>O.getTodoLists(e)}),Wm=e=>he({queryKey:X.todoList(e),queryFn:()=>O.getTodoList(e),enabled:!!e}),Vm=()=>{const e=Y();return ee({mutationFn:r=>O.createTodoList(r),onSuccess:()=>{e.invalidateQueries({queryKey:["todoLists"]})}})},Km=()=>{const e=Y();return ee({mutationFn:({id:r,data:n})=>O.updateTodoList(r,n),onSuccess:(r,{id:n})=>{e.invalidateQueries({queryKey:X.todoList(n)}),e.invalidateQueries({queryKey:["todoLists"]})}})},Gm=()=>{const e=Y();return ee({mutationFn:r=>O.deleteTodoList(r),onSuccess:()=>{e.invalidateQueries({queryKey:["todoLists"]})}})},_m=()=>{const e=Y();return ee({mutationFn:({listId:r,content:n})=>O.addTodoItem(r,n),onMutate:async({listId:r,content:n})=>{await e.cancelQueries({queryKey:X.todoList(r)});const o=e.getQueryData(X.todoList(r));if(o){const s={id:`temp-${Date.now()}`,listId:r,content:n,completed:!1,createdAt:new Date().toISOString(),updatedAt:new Date().toISOString()};e.setQueryData(X.todoList(r),{...o,items:[...o.items,s]})}return{previous:o}},onError:(r,{listId:n},o)=>{o!=null&&o.previous&&e.setQueryData(X.todoList(n),o.previous)},onSettled:(r,n,{listId:o})=>{e.invalidateQueries({queryKey:X.todoList(o)}),e.invalidateQueries({queryKey:["todoLists"]})}})},Qm=()=>{const e=Y();return ee({mutationFn:({itemId:r})=>O.toggleTodoItem(r),onMutate:async({itemId:r,listId:n})=>{await e.cancelQueries({queryKey:X.todoList(n)});const o=e.getQueryData(X.todoList(n));return o&&e.setQueryData(X.todoList(n),{...o,items:o.items.map(s=>s.id===r?{...s,completed:!s.completed,completedAt:s.completed?void 0:new Date().toISOString()}:s)}),{previous:o}},onError:(r,{listId:n},o)=>{o!=null&&o.previous&&e.setQueryData(X.todoList(n),o.previous)},onSettled:(r,n,{listId:o})=>{e.invalidateQueries({queryKey:X.todoList(o)}),e.invalidateQueries({queryKey:["todoLists"]})}})},Jm=()=>{const e=Y();return ee({mutationFn:({itemId:r,data:n})=>O.updateTodoItem(r,n),onMutate:async({itemId:r,listId:n,data:o})=>{await e.cancelQueries({queryKey:X.todoList(n)});const s=e.getQueryData(X.todoList(n));return s&&e.setQueryData(X.todoList(n),{...s,items:s.items.map(i=>i.id===r?{...i,...o}:i)}),{previous:s}},onError:(r,{listId:n},o)=>{o!=null&&o.previous&&e.setQueryData(X.todoList(n),o.previous)},onSettled:(r,n,{listId:o})=>{e.invalidateQueries({queryKey:X.todoList(o)}),e.invalidateQueries({queryKey:["todoLists"]})}})},Ym=()=>{const e=Y();return ee({mutationFn:({itemId:r})=>O.deleteTodoItem(r),onMutate:async({itemId:r,listId:n})=>{await e.cancelQueries({queryKey:X.todoList(n)});const o=e.getQueryData(X.todoList(n));return o&&e.setQueryData(X.todoList(n),{...o,items:o.items.filter(s=>s.id!==r)}),{previous:o}},onError:(r,{listId:n},o)=>{o!=null&&o.previous&&e.setQueryData(X.todoList(n),o.previous)},onSettled:(r,n,{listId:o})=>{e.invalidateQueries({queryKey:X.todoList(o)}),e.invalidateQueries({queryKey:["todoLists"]})}})},Zm=a.div`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: ${e=>e.theme.spacing.md};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.$isEditing?e.theme.colors.primary.neonCarrot:e.$completed?e.theme.colors.status.success:e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  transition: all ${e=>e.theme.animation.normal} ease;
  cursor: pointer;
  animation: slideIn ${e=>e.theme.animation.normal} ease;

  &:hover {
    border-color: ${e=>(e.$isEditing,e.theme.colors.primary.neonCarrot)};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}30;
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateX(-8px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }
`,Xm=a.button`
  width: 24px;
  height: 24px;
  min-width: 24px;
  border-radius: 50%;
  border: 2px solid ${e=>e.$completed?e.theme.colors.status.success:e.theme.colors.primary.anakiwa};
  background: ${e=>e.$completed?e.theme.colors.status.success:"transparent"};
  color: ${e=>e.theme.colors.background};
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all ${e=>e.theme.animation.fast} ease;
  padding: 0;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 0 8px ${e=>e.$completed?e.theme.colors.status.success:e.theme.colors.primary.anakiwa};
  }

  &:active {
    transform: scale(0.95);
  }
`,eh=a.div`
  flex: 1;
  font-family: ${e=>e.theme.typography.fontFamily};
  font-size: 14px;
  color: ${e=>e.$completed?e.theme.colors.text.muted:e.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing};
  text-decoration: ${e=>e.$completed?"line-through":"none"};
  user-select: none;
`,th=a.input`
  flex: 1;
  font-family: ${e=>e.theme.typography.fontFamily};
  font-size: 14px;
  color: ${e=>e.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing};
  background: ${e=>e.theme.colors.surface.medium};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 8px 12px;
  outline: none;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }
`,rh=a.button`
  width: 24px;
  height: 24px;
  min-width: 24px;
  border-radius: 50%;
  border: 2px solid ${e=>e.theme.colors.status.error};
  background: transparent;
  color: ${e=>e.theme.colors.status.error};
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: ${e=>e.$visible?1:0};
  transition: all ${e=>e.theme.animation.fast} ease;
  padding: 0;

  &:hover {
    background: ${e=>e.theme.colors.status.error};
    color: ${e=>e.theme.colors.background};
    transform: scale(1.1);
    box-shadow: 0 0 8px ${e=>e.theme.colors.status.error};
  }

  &:active {
    transform: scale(0.95);
  }
`,nh=({item:e,onToggle:r,onUpdate:n,onDelete:o})=>{const[s,i]=b.useState(!1),[l,c]=b.useState(e.content),[m,d]=b.useState(!1),h=b.useRef(null);b.useEffect(()=>{s&&h.current&&(h.current.focus(),h.current.select())},[s]);const y=x=>{x.stopPropagation(),e.completed||i(!0)},j=()=>{const x=l.trim();x&&x!==e.content&&n(e.id,x),i(!1)},f=()=>{c(e.content),i(!1)},p=x=>{x.key==="Enter"?j():x.key==="Escape"&&f()},g=x=>{x.stopPropagation(),r(e.id)},u=x=>{x.stopPropagation(),o(e.id)};return t.jsxs(Zm,{$completed:e.completed,$isEditing:s,onMouseEnter:()=>d(!0),onMouseLeave:()=>d(!1),children:[t.jsx(Xm,{$completed:e.completed,onClick:g,role:"checkbox","aria-label":e.completed?"Mark incomplete":"Mark complete","aria-checked":e.completed,children:e.completed&&"✓"}),s?t.jsx(th,{ref:h,value:l,onChange:x=>c(x.target.value),onKeyDown:p,onBlur:j}):t.jsx(eh,{$completed:e.completed,onClick:y,children:e.content}),t.jsx(rh,{$visible:m,onClick:u,"aria-label":"Delete task",children:"×"})]})},oh=a.div`
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
`,ah=a.div`
  flex: 1;
  height: 10px;
  background: ${e=>e.theme.colors.surface.medium};
  border-radius: ${e=>e.theme.borderRadius.pill};
  overflow: hidden;
  position: relative;
`,sh=a.div`
  height: 100%;
  width: ${e=>e.$percentage}%;
  background: ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.pill};
  transition: width ${e=>e.theme.animation.normal} ease;
  box-shadow: 0 0 8px ${e=>e.theme.colors.primary.neonCarrot}40;
`,ih=a.span`
  font-family: ${e=>e.theme.typography.fontFamily};
  font-size: 12px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing};
  min-width: 42px;
  text-align: right;
`,lh=({percentage:e})=>{const r=Math.min(100,Math.max(0,e));return t.jsxs(oh,{children:[t.jsx(ah,{children:t.jsx(sh,{$percentage:r})}),t.jsxs(ih,{children:[Math.round(r),"%"]})]})},ch=a.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
  gap: 16px;
`,dh=a.div`
  font-size: 48px;
  line-height: 1;
  opacity: 0.6;
  filter: grayscale(0.3);
`,ph=a.h3`
  font-family: ${e=>e.theme.typography.fontFamily};
  font-size: 20px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing};
  margin: 0;
`,mh=a.p`
  font-family: ${e=>e.theme.typography.fontFamily};
  font-size: 14px;
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing};
  margin: 0;
  max-width: 320px;
`,ho=({title:e,message:r,icon:n="📋"})=>t.jsxs(ch,{children:[t.jsx(dh,{children:n}),t.jsx(ph,{children:e}),t.jsx(mh,{children:r})]}),hh=ie`
  from { opacity: 0; }
  to { opacity: 1; }
`,uh=ie`
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
`,gh=a.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  z-index: ${e=>e.theme.zIndex.modal};
  display: flex;
  align-items: center;
  justify-content: center;
  animation: ${hh} 150ms ease;
`,xh=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.lilac};
  border-radius: ${e=>e.theme.borderRadius.lg};
  width: ${e=>e.width||"480px"};
  max-width: 90vw;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: ${uh} 200ms ease;
`,uo={primary:"#FF9933",secondary:"#CC99CC",accent:"#99CCFF",danger:"#FF5555"},fh=a.div`
  background: ${e=>uo[e.variant]||uo.primary};
  padding: 0 ${e=>e.theme.spacing.md};
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: ${e=>e.theme.lcars.buttonRadius};
  margin: ${e=>e.theme.spacing.sm};
  margin-bottom: 0;
`,yh=a.span`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.theme.colors.text.inverse};
`,bh=a.button`
  background: none;
  border: none;
  color: ${e=>e.theme.colors.text.inverse};
  font-size: 18px;
  cursor: pointer;
  padding: 0 4px;
  font-weight: bold;
  line-height: 1;
  opacity: 0.8;

  &:hover {
    opacity: 1;
  }
`,jh=a.div`
  padding: ${e=>e.theme.spacing.lg};
  overflow-y: auto;
  flex: 1;
`,Ra=({isOpen:e,onClose:r,title:n,variant:o="primary",children:s,width:i})=>{const l=b.useRef(null),c=b.useCallback(m=>{m.key==="Escape"&&r()},[r]);return b.useEffect(()=>{if(e)return document.addEventListener("keydown",c),document.body.style.overflow="hidden",()=>{document.removeEventListener("keydown",c),document.body.style.overflow=""}},[e,c]),e?Go.createPortal(t.jsx(gh,{onClick:m=>{m.target===m.currentTarget&&r()},children:t.jsxs(xh,{ref:l,width:i,role:"dialog","aria-modal":"true",children:[n&&t.jsxs(fh,{variant:o,children:[t.jsx(yh,{children:n}),t.jsx(bh,{onClick:r,"aria-label":"Close",children:"×"})]}),t.jsx(jh,{children:s})]})}),document.body):null},vh=a.p`
  color: ${e=>e.theme.colors.text.light};
  font-size: ${e=>e.theme.typography.fontSize.md};
  line-height: ${e=>e.theme.typography.lineHeight.normal};
  margin: 0 0 ${e=>e.theme.spacing.lg} 0;
`,$h=a.div`
  display: flex;
  justify-content: flex-end;
  gap: ${e=>e.theme.spacing.md};
`,wh=({isOpen:e,onClose:r,onConfirm:n,title:o,message:s,confirmLabel:i="Confirm",cancelLabel:l="Cancel",variant:c="primary",isLoading:m=!1})=>t.jsxs(Ra,{isOpen:e,onClose:r,title:o,variant:c==="danger"?"danger":"primary",children:[t.jsx(vh,{children:s}),t.jsxs($h,{children:[t.jsx(k,{variant:"secondary",onClick:r,disabled:m,children:l}),t.jsx(k,{variant:c==="danger"?"danger":"primary",onClick:n,disabled:m,children:m?"Processing...":i})]})]}),Ch=ie`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
`,Sh=ie`
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
`,kh=ie`
  from { opacity: 0; transform: translateX(-12px); }
  to { opacity: 1; transform: translateX(0); }
`,ir="768px",go="300px",Th="3px",xo=a.div`
  display: flex;
  min-height: calc(100vh - 140px);
  gap: ${Th};

  @media (max-width: ${ir}) {
    flex-direction: column;
    gap: 0;
  }
`,Ah=a.aside`
  width: ${go};
  min-width: ${go};
  background: ${e=>e.theme.colors.surface.dark};
  display: flex;
  flex-direction: column;
  overflow: hidden;

  @media (max-width: ${ir}) {
    width: 100%;
    min-width: 100%;
    display: ${e=>e.$hidden?"none":"flex"};
  }
`,Fh=a.section`
  flex: 1;
  background: ${e=>e.theme.colors.background};
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: ${Sh} 300ms ease;

  @media (max-width: ${ir}) {
    display: ${e=>e.$hidden?"none":"flex"};
  }
`,Eh=a.div`
  padding: ${e=>e.theme.spacing.lg} ${e=>e.theme.spacing.md};
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
  flex-shrink: 0;
`,Lh=a.h2`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.extraWide};
  margin: 0;
`,zh=a.select`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  color: ${e=>e.theme.colors.text.light};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 6px 10px;
  outline: none;
  cursor: pointer;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${e=>e.theme.colors.surface.dark};
  }
`,Dh=a.div`
  flex: 1;
  overflow-y: auto;
  padding: 0 ${e=>e.theme.spacing.md} ${e=>e.theme.spacing.md};
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,Ih=a.button`
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  padding: ${e=>e.theme.spacing.md};
  background: ${e=>e.$active?e.theme.colors.surface.medium:"transparent"};
  border: 2px solid ${e=>e.$active?e.theme.colors.primary.neonCarrot:e.theme.colors.surface.medium};
  border-radius: ${e=>e.theme.borderRadius.md};
  cursor: pointer;
  text-align: left;
  transition: all 200ms ease;
  animation: ${kh} 300ms ease both;

  ${e=>e.$active&&z`
    box-shadow: 0 0 10px ${e.theme.colors.primary.neonCarrot}30;
  `}

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    background: ${e=>e.theme.colors.surface.medium};
  }
`,Mh=a.span`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
`,Pr=a.span`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 11px;
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
`,fo=a.span`
  display: inline-block;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 10px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  color: ${e=>e.theme.colors.text.inverse};
  background: ${e=>e.$type==="boat"?e.theme.colors.primary.anakiwa:e.theme.colors.primary.neonCarrot};
  padding: 2px 10px;
  border-radius: ${e=>e.theme.borderRadius.pill};
`,Rh=a.div`
  padding: ${e=>e.theme.spacing.lg};
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
  flex-shrink: 0;
  border-bottom: 2px solid ${e=>e.theme.colors.surface.medium};
`,Nh=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
  flex-wrap: wrap;
`,Ph=a.h2`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.xl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  margin: 0;
  cursor: pointer;
  transition: color 200ms ease;

  &:hover {
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }
`,Bh=a.input`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.xl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 4px 12px;
  outline: none;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }
`,Oh=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 12px;
  color: ${e=>e.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
`,Uh=a.button`
  display: none;

  @media (max-width: ${ir}) {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: none;
    border: none;
    color: ${e=>e.theme.colors.primary.anakiwa};
    font-family: ${e=>e.theme.typography.fontFamily.primary};
    font-size: ${e=>e.theme.typography.fontSize.sm};
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
    padding: 0;
    cursor: pointer;
    margin-bottom: ${e=>e.theme.spacing.sm};
  }
`,qh=a.div`
  flex: 1;
  overflow-y: auto;
  padding: ${e=>e.theme.spacing.lg};
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,Hh=a.form`
  display: flex;
  gap: ${e=>e.theme.spacing.sm};
  margin-bottom: ${e=>e.theme.spacing.md};
  flex-shrink: 0;
`,Wh=a.input`
  flex: 1;
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 14px;
  color: ${e=>e.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.muted};
    opacity: 0.6;
  }
`,Vh=a.div`
  padding: ${e=>e.theme.spacing.md} ${e=>e.theme.spacing.lg};
  border-top: 2px solid ${e=>e.theme.colors.surface.medium};
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
`,yo=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.lg};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.extraWide};
  text-align: center;
  padding: 80px 24px;
  animation: ${Ch} 1.5s ease infinite;
`,Br=a.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: ${e=>e.theme.spacing.md};
`,Or=a.label`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 11px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  color: ${e=>e.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.wide};
`,Kh=a.input`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 14px;
  color: ${e=>e.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }
`,bo=a.select`
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: 14px;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${e=>e.theme.typography.letterSpacing.normal};
  color: ${e=>e.theme.colors.text.light};
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;
  cursor: pointer;

  &:focus {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${e=>e.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${e=>e.theme.colors.surface.dark};
  }
`,Gh=a.div`
  display: flex;
  justify-content: flex-end;
  gap: ${e=>e.theme.spacing.md};
  margin-top: ${e=>e.theme.spacing.md};
`,_h=()=>{const[e,r]=Xa(),n=e.get("list")||"",{data:o,isLoading:s}=Hm(),{data:i,isLoading:l}=Wm(n),{data:c}=ye(),m=Vm(),d=Km(),h=Gm(),y=_m(),j=Qm(),f=Jm(),p=Ym(),[g,u]=b.useState("all"),[x,v]=b.useState(!1),[$,w]=b.useState(!1),[A,T]=b.useState(!1),[I,D]=b.useState(""),[U,F]=b.useState(""),[R,H]=b.useState(""),[G,J]=b.useState("general"),[B,te]=b.useState(""),be=!!n,N=b.useRef(null),C=b.useRef(null);b.useEffect(()=>{!n&&o&&o.length>0&&r({list:o[0].id},{replace:!0})},[o,n,r]),b.useEffect(()=>{A&&N.current&&(N.current.focus(),N.current.select())},[A]);const W=b.useMemo(()=>o?g==="all"?o:o.filter(P=>P.type===g):[],[o,g]),K=b.useMemo(()=>i!=null&&i.items?[...i.items].sort((P,ue)=>P.completed===ue.completed?0:P.completed?1:-1):[],[i]),le=(i==null?void 0:i.items.filter(P=>P.completed).length)??0,ae=(i==null?void 0:i.items.length)??0,Te=ae>0?le/ae*100:0,qe=P=>{if(!P||!c)return"";const ue=c.find(at=>at.id===P);return(ue==null?void 0:ue.name)??""},Ae=P=>{r({list:P})},dr=()=>{i&&(D(i.title),T(!0))},bn=()=>{const P=I.trim();P&&P!==(i==null?void 0:i.title)&&d.mutate({id:n,data:{title:P}}),T(!1)},Ua=P=>{P.key==="Enter"?bn():P.key==="Escape"&&T(!1)},qa=P=>{var at;P.preventDefault();const ue=U.trim();!ue||!n||(y.mutate({listId:n,content:ue}),F(""),(at=C.current)==null||at.focus())},Ha=P=>{j.mutate({itemId:P,listId:n})},Wa=(P,ue)=>{f.mutate({itemId:P,listId:n,data:{content:ue}})},Va=P=>{p.mutate({itemId:P,listId:n})},Ka=()=>{h.mutate(n,{onSuccess:()=>{w(!1),r({},{replace:!0})}})},jn=()=>{const P=R.trim();P&&m.mutate({title:P,type:G,boatId:G==="boat"&&B||void 0},{onSuccess:ue=>{v(!1),H(""),J("general"),te(""),r({list:ue.id})}})},Ga=()=>{r({},{replace:!0})};return s?t.jsx(xo,{children:t.jsx(yo,{children:"Accessing Database..."})}):t.jsxs(xo,{children:[t.jsxs(Ah,{$hidden:be,children:[t.jsxs(Eh,{children:[t.jsx(Lh,{children:"Task Lists"}),t.jsx(Q,{children:t.jsx(k,{variant:"secondary",size:"sm",onClick:()=>v(!0),children:"New List"})}),t.jsxs(zh,{value:g,onChange:P=>u(P.target.value),"aria-label":"Filter by type",children:[t.jsx("option",{value:"all",children:"All Types"}),t.jsx("option",{value:"general",children:"General"}),t.jsx("option",{value:"boat",children:"Boat"})]})]}),t.jsx(Dh,{children:W.length===0?t.jsx(Pr,{style:{textAlign:"center",padding:"24px 0"},children:o&&o.length>0?"No matching lists":"No lists yet"}):W.map((P,ue)=>{const at=P.items.filter(Qa=>Qa.completed).length,_a=P.items.length;return t.jsxs(Ih,{$active:P.id===n,onClick:()=>Ae(P.id),style:{animationDelay:`${ue*40}ms`},"aria-current":P.id===n?"true":void 0,children:[t.jsx(Mh,{children:P.title}),t.jsxs("div",{style:{display:"flex",alignItems:"center",gap:"8px"},children:[t.jsx(fo,{$type:P.type,children:P.type==="boat"?`Boat - ${qe(P.boatId)}`:"General"}),t.jsxs(Pr,{children:[at,"/",_a," done"]})]})]},P.id)})})]}),t.jsx(Fh,{$hidden:!be&&!n&&!1,children:n?l?t.jsx(yo,{children:"Loading List Data..."}):i?t.jsxs(t.Fragment,{children:[t.jsxs(Rh,{children:[t.jsx(Uh,{onClick:Ga,children:"← Back to Lists"}),t.jsxs(Nh,{children:[A?t.jsx(Bh,{ref:N,value:I,onChange:P=>D(P.target.value),onKeyDown:Ua,onBlur:bn}):t.jsx(Ph,{onClick:dr,children:i.title}),t.jsx(fo,{$type:i.type,children:i.type==="boat"?`Boat - ${qe(i.boatId)}`:"General"})]}),t.jsxs(Oh,{children:[le," of ",ae," completed",ae>0&&` — ${Math.round(Te)}%`]}),t.jsx(lh,{percentage:Te})]}),t.jsxs(qh,{children:[t.jsx(Q,{fallback:null,children:t.jsxs(Hh,{onSubmit:qa,children:[t.jsx(Wh,{ref:C,value:U,onChange:P=>F(P.target.value),placeholder:"Add new task...","aria-label":"New task content"}),t.jsx(k,{variant:"primary",size:"sm",type:"submit",onClick:()=>{},children:"Add"})]})}),K.length===0?t.jsx(Pr,{style:{textAlign:"center",padding:"24px 0"},children:"No items yet. Add your first task above."}):K.map(P=>t.jsx(nh,{item:P,onToggle:Ha,onUpdate:Wa,onDelete:Va},P.id))]}),t.jsx(Vh,{children:t.jsx(Q,{children:t.jsx(k,{variant:"danger",size:"sm",onClick:()=>w(!0),children:"Delete List"})})})]}):t.jsx(ho,{title:"List Not Found",message:"The selected list could not be loaded. It may have been deleted."}):t.jsx(ho,{title:o&&o.length>0?"Select a List":"Create Your First List",message:o&&o.length>0?"Choose a task list from the sidebar to view its items":"Get started by creating a new task list using the button on the left"})}),t.jsxs(Ra,{isOpen:x,onClose:()=>v(!1),title:"Create Task List",children:[t.jsxs(Br,{children:[t.jsx(Or,{htmlFor:"create-title",children:"Title"}),t.jsx(Kh,{id:"create-title",value:R,onChange:P=>H(P.target.value),placeholder:"Enter list title...",autoFocus:!0,onKeyDown:P=>{P.key==="Enter"&&jn()}})]}),t.jsxs(Br,{children:[t.jsx(Or,{htmlFor:"create-type",children:"Type"}),t.jsxs(bo,{id:"create-type",value:G,onChange:P=>J(P.target.value),children:[t.jsx("option",{value:"general",children:"General"}),t.jsx("option",{value:"boat",children:"Boat"})]})]}),G==="boat"&&t.jsxs(Br,{children:[t.jsx(Or,{htmlFor:"create-boat",children:"Vessel"}),t.jsxs(bo,{id:"create-boat",value:B,onChange:P=>te(P.target.value),children:[t.jsx("option",{value:"",children:"Select a vessel..."}),c==null?void 0:c.map(P=>t.jsx("option",{value:P.id,children:P.name},P.id))]})]}),t.jsxs(Gh,{children:[t.jsx(k,{variant:"secondary",size:"sm",onClick:()=>v(!1),children:"Cancel"}),t.jsx(k,{variant:"primary",size:"sm",onClick:jn,disabled:!R.trim()||G==="boat"&&!B,children:"Create"})]})]}),t.jsx(wh,{isOpen:$,onClose:()=>w(!1),onConfirm:Ka,title:"Delete Task List",message:`Permanently delete "${(i==null?void 0:i.title)??""}" and all its items? This action cannot be undone.`,confirmLabel:"Delete",cancelLabel:"Cancel",variant:"danger",isLoading:h.isPending})]})};function Na(e){return he({queryKey:["maintenance-templates",e],queryFn:()=>O.getMaintenanceTemplates(e)})}function Pa(e,r){return he({queryKey:["maintenance-template",e],queryFn:()=>O.getMaintenanceTemplate(e),enabled:(r==null?void 0:r.enabled)!==void 0?r.enabled:!!e})}function Qh(){const e=Y();return ee({mutationFn:r=>O.createMaintenanceTemplate(r),onSuccess:()=>{e.invalidateQueries({queryKey:["maintenance-templates"]})}})}function Jh(){const e=Y();return ee({mutationFn:({id:r,data:n})=>O.updateMaintenanceTemplate(r,n),onSuccess:(r,{id:n})=>{e.invalidateQueries({queryKey:["maintenance-template",n]}),e.invalidateQueries({queryKey:["maintenance-templates"]})}})}function Yh(){const e=Y();return ee({mutationFn:r=>O.deleteMaintenanceTemplate(r),onSuccess:()=>{e.invalidateQueries({queryKey:["maintenance-templates"]}),e.invalidateQueries({queryKey:["maintenance-events"]})}})}function yn(e){return he({queryKey:["maintenance-events","upcoming",e],queryFn:()=>O.getUpcomingMaintenanceEvents(e)})}function Ba(e){return he({queryKey:["maintenance-events","completed",e],queryFn:()=>O.getCompletedMaintenanceEvents(e)})}function Zh(e){return he({queryKey:["maintenance-event",e],queryFn:()=>O.getMaintenanceEvent(e),enabled:!!e})}function Xh(){const e=Y();return ee({mutationFn:({id:r,data:n})=>O.completeMaintenanceEvent(r,n),onSuccess:()=>{e.invalidateQueries({queryKey:["maintenance-events"]})}})}const eu=a.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,tu=a.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`,ru=a.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`,Ur=a(k)`
  background-color: ${e=>e.active?e.theme.colors.primary.neonCarrot:e.theme.colors.primary.lilac};
  opacity: ${e=>e.active?1:.7};
`,nu=a(L)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`,jo=a.div`
  display: grid;
  gap: 15px;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,vo=a(L)`
  padding: 15px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background-color: ${e=>e.theme.colors.primary.lilac}20;
  }
`,$o=a.div`
  display: flex;
  justify-content: between;
  align-items: flex-start;
  margin-bottom: 10px;
`,wo=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin: 0;
  font-size: 18px;
  flex: 1;
`,Co=a.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 14px;
  color: ${e=>e.theme.colors.text.secondary};
`,So=a.span`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${e=>{switch(e.status){case"active":return e.theme.colors.primary.anakiwa;case"inactive":return e.theme.colors.text.secondary;case"due":return e.theme.colors.primary.neonCarrot;case"overdue":return"#ff4444";case"completed":return"#44ff44";default:return e.theme.colors.text.secondary}}};
  color: ${e=>e.theme.colors.background};
`,ou=a.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
`,au=a.select`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
`;function su(){const[e,r]=b.useState("templates"),[n,o]=b.useState(""),{data:s=[]}=ye(),{data:i=[],isLoading:l}=Na(n||void 0),{data:c=[],isLoading:m}=yn(n||void 0),{data:d=[],isLoading:h}=Ba(n||void 0),y=v=>{if(!v)return"One-time";const{type:$,interval:w}=v,A=w===1?$.slice(0,-1):$;return`Every ${w} ${A}`},j=v=>v?new Intl.NumberFormat("en-US",{style:"currency",currency:"USD"}).format(v):"N/A",f=v=>{if(!v)return"N/A";const $=Math.floor(v/60),w=v%60;return $>0?`${$}h ${w}m`:`${w}m`},p=v=>{if(v.completedAt)return"completed";const $=new Date(v.dueDate),w=new Date,A=Math.ceil(($.getTime()-w.getTime())/(1e3*60*60*24));return A<0?"overdue":A<=7?"due":"active"},g=()=>t.jsx(jo,{children:i.map(v=>{var $;return t.jsx(oe,{to:`/maintenance/templates/${v.id}`,style:{textDecoration:"none"},children:t.jsxs(vo,{children:[t.jsxs($o,{children:[t.jsx(wo,{children:v.title}),t.jsx(So,{status:v.isActive?"active":"inactive",children:v.isActive?"Active":"Inactive"})]}),t.jsxs(Co,{children:[t.jsxs("div",{children:[t.jsx("strong",{children:"Boat:"})," ",(($=v.boat)==null?void 0:$.name)||"Unknown"]}),v.component&&t.jsxs("div",{children:[t.jsx("strong",{children:"Component:"})," ",v.component]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Recurrence:"})," ",y(v.recurrence)]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Est. Cost:"})," ",j(v.estimatedCost)]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Est. Time:"})," ",f(v.estimatedTime)]})]}),v.description&&t.jsx("div",{style:{marginTop:"10px",fontSize:"14px",color:"#ccc"},children:v.description})]})},v.id)})}),u=(v,$=!1)=>t.jsx(jo,{children:v.map(w=>{var A,T,I,D;return t.jsx(oe,{to:`/maintenance/events/${w.id}`,style:{textDecoration:"none"},children:t.jsxs(vo,{children:[t.jsxs($o,{children:[t.jsx(wo,{children:((A=w.template)==null?void 0:A.title)||"Unknown Task"}),t.jsx(So,{status:p(w),children:p(w)})]}),t.jsxs(Co,{children:[t.jsxs("div",{children:[t.jsx("strong",{children:"Boat:"})," ",((I=(T=w.template)==null?void 0:T.boat)==null?void 0:I.name)||"Unknown"]}),((D=w.template)==null?void 0:D.component)&&t.jsxs("div",{children:[t.jsx("strong",{children:"Component:"})," ",w.template.component]}),t.jsxs("div",{children:[t.jsx("strong",{children:"Due Date:"})," ",new Date(w.dueDate).toLocaleDateString()]}),$&&w.completedAt&&t.jsxs("div",{children:[t.jsx("strong",{children:"Completed:"})," ",new Date(w.completedAt).toLocaleDateString()]}),w.actualCost&&t.jsxs("div",{children:[t.jsx("strong",{children:"Actual Cost:"})," ",j(w.actualCost)]}),w.actualTime&&t.jsxs("div",{children:[t.jsx("strong",{children:"Actual Time:"})," ",f(w.actualTime)]})]}),w.notes&&t.jsx("div",{style:{marginTop:"10px",fontSize:"14px",color:"#ccc"},children:w.notes})]})},w.id)})}),x=l||m||h;return t.jsxs(eu,{children:[t.jsxs(Me,{children:[t.jsx(E,{label:"System Status",value:"OPERATIONAL"}),t.jsx(E,{label:"Active Templates",value:i.filter(v=>v.isActive).length.toString()}),t.jsx(E,{label:"Upcoming Events",value:c.length.toString()}),t.jsx(E,{label:"Overdue Events",value:c.filter(v=>p(v)==="overdue").length.toString()})]}),t.jsxs(tu,{children:[t.jsx(q,{children:"Maintenance Management"}),t.jsxs(ou,{children:[t.jsxs(au,{value:n,onChange:v=>o(v.target.value),children:[t.jsx("option",{value:"",children:"All Boats"}),s.map(v=>t.jsx("option",{value:v.id,children:v.name},v.id))]}),t.jsx(Q,{children:t.jsx(oe,{to:"/maintenance/templates/new",children:t.jsx(k,{children:"New Template"})})})]}),t.jsxs(ru,{children:[t.jsxs(Ur,{active:e==="templates",onClick:()=>r("templates"),children:["Templates (",i.length,")"]}),t.jsxs(Ur,{active:e==="upcoming",onClick:()=>r("upcoming"),children:["Upcoming (",c.length,")"]}),t.jsxs(Ur,{active:e==="completed",onClick:()=>r("completed"),children:["Completed (",d.length,")"]})]}),t.jsx(nu,{children:x?t.jsx("div",{style:{textAlign:"center",padding:"40px"},children:t.jsx("div",{style:{color:"#ff9966",fontSize:"18px"},children:"Loading maintenance data..."})}):t.jsxs(t.Fragment,{children:[e==="templates"&&g(),e==="upcoming"&&u(c),e==="completed"&&u(d,!0)]})})]})]})}const qr=a.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
`,Hr=a.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`,Wr=a(L)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`,iu=a.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`,lu=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
`,Vr=a(L)`
  padding: 15px;
`,Ht=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin: 0 0 15px 0;
  font-size: 16px;
  text-transform: uppercase;
`,Ne=a.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  
  &:last-child {
    margin-bottom: 0;
  }
`,Pe=a.span`
  color: ${e=>e.theme.colors.text.secondary};
  font-weight: bold;
`,Be=a.span`
  color: ${e=>e.theme.colors.text.primary};
`,cu=a.span`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${e=>e.active?e.theme.colors.primary.anakiwa:e.theme.colors.text.secondary};
  color: ${e=>e.theme.colors.background};
`,du=a.div`
  background-color: ${e=>e.theme.colors.background}40;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid ${e=>e.theme.colors.primary.neonCarrot};
  margin-bottom: 20px;
  line-height: 1.5;
`,pu=a.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: 18px;
`,mu=a.div`
  padding: 20px;
  text-align: center;
`;function hu(){var h,y;const{id:e}=Ge(),r=de(),{data:n,isLoading:o,error:s}=Pa(e),i=Yh(),l=async()=>{if(!n)return;if(window.confirm(`Are you sure you want to delete the template "${n.title}"? This will also delete all future maintenance events for this template.`))try{await i.mutateAsync(n.id),r("/maintenance")}catch(f){console.error("Failed to delete template:",f),alert("Failed to delete template. Please try again.")}},c=j=>{if(!j)return"One-time";const{type:f,interval:p}=j,g=p===1?f.slice(0,-1):f;return`Every ${p} ${g}`},m=j=>j?new Intl.NumberFormat("en-US",{style:"currency",currency:"USD"}).format(j):"Not specified",d=j=>{if(!j)return"Not specified";const f=Math.floor(j/60),p=j%60;return f>0?`${f}h ${p}m`:`${p}m`};return o?t.jsxs(qr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"LOADING"})}),t.jsxs(Hr,{children:[t.jsx(q,{children:"Maintenance Template"}),t.jsx(Wr,{children:t.jsx(pu,{children:"Loading template details..."})})]})]}):s||!n?t.jsxs(qr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"ERROR"})}),t.jsxs(Hr,{children:[t.jsx(q,{children:"Maintenance Template"}),t.jsx(Wr,{children:t.jsxs(mu,{children:[t.jsx(Se,{type:"error",children:"Template not found or failed to load."}),t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{children:"Back to Maintenance"})})]})})]})]}):t.jsxs(qr,{children:[t.jsxs(Me,{children:[t.jsx(E,{label:"Template Status",value:n.isActive?"ACTIVE":"INACTIVE"}),t.jsx(E,{label:"Boat",value:((h=n.boat)==null?void 0:h.name)||"Unknown"}),t.jsx(E,{label:"Component",value:n.component||"General"}),t.jsx(E,{label:"Recurrence",value:c(n.recurrence)})]}),t.jsxs(Hr,{children:[t.jsx(q,{children:n.title}),t.jsxs(iu,{children:[t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{children:"Back to List"})}),t.jsx(Q,{children:t.jsx(oe,{to:`/maintenance/templates/${n.id}/edit`,children:t.jsx(k,{children:"Edit Template"})})}),t.jsx(Q,{children:t.jsx(k,{onClick:l,disabled:i.isPending,variant:"danger",children:i.isPending?"Deleting...":"Delete Template"})})]}),t.jsxs(Wr,{children:[n.description&&t.jsxs(du,{children:[t.jsx("strong",{children:"Description:"}),t.jsx("br",{}),n.description]}),t.jsxs(lu,{children:[t.jsxs(Vr,{children:[t.jsx(Ht,{children:"Basic Information"}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Title:"}),t.jsx(Be,{children:n.title})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Boat:"}),t.jsx(Be,{children:((y=n.boat)==null?void 0:y.name)||"Unknown"})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Component:"}),t.jsx(Be,{children:n.component||"General"})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Status:"}),t.jsx(Be,{children:t.jsx(cu,{active:n.isActive,children:n.isActive?"Active":"Inactive"})})]})]}),t.jsxs(Vr,{children:[t.jsx(Ht,{children:"Schedule & Estimates"}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Recurrence:"}),t.jsx(Be,{children:c(n.recurrence)})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Estimated Cost:"}),t.jsx(Be,{children:m(n.estimatedCost)})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Estimated Time:"}),t.jsx(Be,{children:d(n.estimatedTime)})]})]}),t.jsxs(Vr,{children:[t.jsx(Ht,{children:"Timestamps"}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Created:"}),t.jsx(Be,{children:new Date(n.createdAt).toLocaleString()})]}),t.jsxs(Ne,{children:[t.jsx(Pe,{children:"Updated:"}),t.jsx(Be,{children:new Date(n.updatedAt).toLocaleString()})]})]})]}),t.jsxs("div",{style:{marginTop:"30px"},children:[t.jsx(Ht,{children:"Related Events"}),t.jsx("p",{style:{color:"#ccc",marginBottom:"20px"},children:"View upcoming and completed maintenance events generated from this template."}),t.jsxs("div",{style:{display:"flex",gap:"10px"},children:[t.jsx(oe,{to:`/maintenance?tab=upcoming&template=${n.id}`,children:t.jsx(k,{children:"View Upcoming Events"})}),t.jsx(oe,{to:`/maintenance?tab=completed&template=${n.id}`,children:t.jsx(k,{children:"View Completed Events"})})]})]})]})]})]})}const Kr=a.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
`,Gr=a.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`,_r=a(L)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`,uu=a.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`,gu=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
`,Qr=a(L)`
  padding: 15px;
`,Wt=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin: 0 0 15px 0;
  font-size: 16px;
  text-transform: uppercase;
`,ze=a.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  
  &:last-child {
    margin-bottom: 0;
  }
`,ve=a.span`
  color: ${e=>e.theme.colors.text.secondary};
  font-weight: bold;
`,De=a.span`
  color: ${e=>e.theme.colors.text.primary};
`,xu=a.span`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${e=>{switch(e.status){case"completed":return"#44ff44";case"overdue":return"#ff4444";case"due":return e.theme.colors.primary.neonCarrot;case"upcoming":return e.theme.colors.primary.anakiwa;default:return e.theme.colors.text.secondary}}};
  color: ${e=>e.theme.colors.background};
`,fu=a.form`
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: ${e=>e.theme.colors.background}40;
  padding: 20px;
  border-radius: 4px;
  border-left: 4px solid ${e=>e.theme.colors.primary.neonCarrot};
`,Jr=a.div`
  display: flex;
  gap: 15px;
  align-items: center;
`,Yr=a.label`
  color: ${e=>e.theme.colors.text.secondary};
  font-weight: bold;
  min-width: 120px;
`,ko=a.input`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;
`,yu=a.textarea`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  flex: 1;
`,bu=a.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: 18px;
`,ju=a.div`
  padding: 20px;
  text-align: center;
`,vu=a.div`
  background-color: ${e=>e.theme.colors.background}40;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid ${e=>e.theme.colors.primary.lilac};
  margin-bottom: 20px;
  line-height: 1.5;
`;function $u(){var u,x,v,$,w,A,T,I,D,U,F;const{id:e}=Ge(),[r,n]=b.useState(!1),[o,s]=b.useState({actualCost:"",actualTime:"",notes:""}),{data:i,isLoading:l,error:c}=Zh(e),m=Xh(),d=R=>{if(R.completedAt)return"completed";const H=new Date(R.dueDate),G=new Date,J=Math.ceil((H.getTime()-G.getTime())/(1e3*60*60*24));return J<0?"overdue":J<=7?"due":"upcoming"},h=R=>R?new Intl.NumberFormat("en-US",{style:"currency",currency:"USD"}).format(R):"Not specified",y=R=>{if(!R)return"Not specified";const H=Math.floor(R/60),G=R%60;return H>0?`${H}h ${G}m`:`${G}m`},j=R=>{if(!R)return"One-time";const{type:H,interval:G}=R,J=G===1?H.slice(0,-1):H;return`Every ${G} ${J}`},f=async R=>{if(R.preventDefault(),!!i)try{const H={};o.actualCost&&(H.actualCost=parseFloat(o.actualCost)),o.actualTime&&(H.actualTime=parseInt(o.actualTime)),o.notes&&(H.notes=o.notes),await m.mutateAsync({id:i.id,data:H}),n(!1)}catch(H){console.error("Failed to complete event:",H),alert("Failed to complete maintenance event. Please try again.")}};if(l)return t.jsxs(Kr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"LOADING"})}),t.jsxs(Gr,{children:[t.jsx(q,{children:"Maintenance Event"}),t.jsx(_r,{children:t.jsx(bu,{children:"Loading event details..."})})]})]});if(c||!i)return t.jsxs(Kr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"ERROR"})}),t.jsxs(Gr,{children:[t.jsx(q,{children:"Maintenance Event"}),t.jsx(_r,{children:t.jsxs(ju,{children:[t.jsx(Se,{type:"error",children:"Event not found or failed to load."}),t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{children:"Back to Maintenance"})})]})})]})]});const p=d(i),g=!!i.completedAt;return t.jsxs(Kr,{children:[t.jsxs(Me,{children:[t.jsx(E,{label:"Event Status",value:p.toUpperCase()}),t.jsx(E,{label:"Boat",value:((x=(u=i.template)==null?void 0:u.boat)==null?void 0:x.name)||"Unknown"}),t.jsx(E,{label:"Due Date",value:new Date(i.dueDate).toLocaleDateString()}),g&&t.jsx(E,{label:"Completed",value:new Date(i.completedAt).toLocaleDateString()})]}),t.jsxs(Gr,{children:[t.jsx(q,{children:((v=i.template)==null?void 0:v.title)||"Maintenance Event"}),t.jsxs(uu,{children:[t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{children:"Back to List"})}),i.template&&t.jsx(oe,{to:`/maintenance/templates/${i.template.id}`,children:t.jsx(k,{children:"View Template"})}),!g&&t.jsx(Q,{children:t.jsx(k,{onClick:()=>n(!r),variant:"accent",children:r?"Cancel Completion":"Complete Event"})})]}),t.jsxs(_r,{children:[t.jsx("div",{style:{marginBottom:"20px"},children:t.jsx(xu,{status:p,children:p.toUpperCase()})}),(($=i.template)==null?void 0:$.description)&&t.jsxs(vu,{children:[t.jsx("strong",{children:"Template Description:"}),t.jsx("br",{}),i.template.description]}),r&&!g&&t.jsxs(fu,{onSubmit:f,children:[t.jsx(Wt,{children:"Complete Maintenance Event"}),t.jsxs(Jr,{children:[t.jsx(Yr,{children:"Actual Cost ($):"}),t.jsx(ko,{type:"number",step:"0.01",value:o.actualCost,onChange:R=>s(H=>({...H,actualCost:R.target.value})),placeholder:"Enter actual cost"})]}),t.jsxs(Jr,{children:[t.jsx(Yr,{children:"Actual Time (minutes):"}),t.jsx(ko,{type:"number",value:o.actualTime,onChange:R=>s(H=>({...H,actualTime:R.target.value})),placeholder:"Enter time in minutes"})]}),t.jsxs(Jr,{children:[t.jsx(Yr,{children:"Notes:"}),t.jsx(yu,{value:o.notes,onChange:R=>s(H=>({...H,notes:R.target.value})),placeholder:"Enter completion notes, observations, or issues encountered"})]}),t.jsxs("div",{style:{display:"flex",gap:"10px",justifyContent:"flex-end"},children:[t.jsx(k,{type:"button",onClick:()=>n(!1),children:"Cancel"}),t.jsx(k,{type:"submit",disabled:m.isPending,variant:"accent",children:m.isPending?"Completing...":"Complete Event"})]})]}),t.jsxs(gu,{children:[t.jsxs(Qr,{children:[t.jsx(Wt,{children:"Event Information"}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Title:"}),t.jsx(De,{children:((w=i.template)==null?void 0:w.title)||"Unknown"})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Boat:"}),t.jsx(De,{children:((T=(A=i.template)==null?void 0:A.boat)==null?void 0:T.name)||"Unknown"})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Component:"}),t.jsx(De,{children:((I=i.template)==null?void 0:I.component)||"General"})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Due Date:"}),t.jsx(De,{children:new Date(i.dueDate).toLocaleDateString()})]}),g&&t.jsxs(ze,{children:[t.jsx(ve,{children:"Completed:"}),t.jsx(De,{children:new Date(i.completedAt).toLocaleDateString()})]})]}),t.jsxs(Qr,{children:[t.jsx(Wt,{children:"Template Information"}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Recurrence:"}),t.jsx(De,{children:j((D=i.template)==null?void 0:D.recurrence)})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Est. Cost:"}),t.jsx(De,{children:h((U=i.template)==null?void 0:U.estimatedCost)})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Est. Time:"}),t.jsx(De,{children:y((F=i.template)==null?void 0:F.estimatedTime)})]})]}),g&&t.jsxs(Qr,{children:[t.jsx(Wt,{children:"Completion Details"}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Actual Cost:"}),t.jsx(De,{children:h(i.actualCost)})]}),t.jsxs(ze,{children:[t.jsx(ve,{children:"Actual Time:"}),t.jsx(De,{children:y(i.actualTime)})]}),i.notes&&t.jsxs("div",{style:{marginTop:"15px"},children:[t.jsx(ve,{style:{display:"block",marginBottom:"5px"},children:"Notes:"}),t.jsx("div",{style:{backgroundColor:"#333",padding:"10px",borderRadius:"4px",whiteSpace:"pre-wrap"},children:i.notes})]})]})]})]})]})]})}const Zr=a.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
`,Xr=a.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`,en=a(L)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`,wu=a.form`
  display: flex;
  flex-direction: column;
  gap: 20px;
`,Vt=a.div`
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
  background-color: ${e=>e.theme.colors.background}40;
  border-radius: 4px;
  border-left: 4px solid ${e=>e.theme.colors.primary.neonCarrot};
`,Kt=a.h3`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  margin: 0 0 10px 0;
  font-size: 16px;
  text-transform: uppercase;
`,Oe=a.div`
  display: flex;
  gap: 15px;
  align-items: flex-start;
`,Ue=a.label`
  color: ${e=>e.theme.colors.text.secondary};
  font-weight: bold;
  min-width: 150px;
  padding-top: 8px;
`,At=a.input`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.anakiwa};
  }
`,To=a.select`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.anakiwa};
  }
`,Cu=a.textarea`
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.anakiwa};
  }
`,Ao=a.input`
  margin-right: 8px;
`,Su=a.div`
  display: flex;
  gap: 10px;
  align-items: center;
  flex: 1;
`,ku=a.div`
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid ${e=>e.theme.colors.primary.neonCarrot}40;
`,Tu=a.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: 18px;
`,Au=a.div`
  padding: 20px;
  text-align: center;
`;function Fo(){const{id:e}=Ge(),r=de(),n=!!e,[o,s]=b.useState({boatId:"",title:"",description:"",component:"",hasRecurrence:!1,recurrenceType:"days",recurrenceInterval:"30",estimatedCost:"",estimatedTime:"",isActive:!0}),{data:i=[]}=ye(),{data:l,isLoading:c}=Pa(e,{enabled:n}),m=Qh(),d=Jh();b.useEffect(()=>{var f,p,g,u,x;l&&n&&s({boatId:l.boatId,title:l.title,description:l.description||"",component:l.component||"",hasRecurrence:!!l.recurrence,recurrenceType:((f=l.recurrence)==null?void 0:f.type)||"days",recurrenceInterval:((g=(p=l.recurrence)==null?void 0:p.interval)==null?void 0:g.toString())||"30",estimatedCost:((u=l.estimatedCost)==null?void 0:u.toString())||"",estimatedTime:((x=l.estimatedTime)==null?void 0:x.toString())||"",isActive:l.isActive})},[l,n]);const h=async f=>{if(f.preventDefault(),!o.boatId||!o.title){alert("Please fill in all required fields (Boat and Title)");return}try{const p={boatId:o.boatId,title:o.title,description:o.description||void 0,component:o.component||void 0,estimatedCost:o.estimatedCost?parseFloat(o.estimatedCost):void 0,estimatedTime:o.estimatedTime?parseInt(o.estimatedTime):void 0};o.hasRecurrence&&(p.recurrence={type:o.recurrenceType,interval:parseInt(o.recurrenceInterval)}),n?(p.isActive=o.isActive,await d.mutateAsync({id:e,data:p})):await m.mutateAsync(p),r("/maintenance")}catch(p){console.error("Failed to save template:",p),alert("Failed to save maintenance template. Please try again.")}},y=(f,p)=>{s(g=>({...g,[f]:p}))};if(n&&c)return t.jsxs(Zr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"LOADING"})}),t.jsxs(Xr,{children:[t.jsx(q,{children:"Edit Maintenance Template"}),t.jsx(en,{children:t.jsx(Tu,{children:"Loading template..."})})]})]});if(n&&!l)return t.jsxs(Zr,{children:[t.jsx(Me,{children:t.jsx(E,{label:"Status",value:"ERROR"})}),t.jsxs(Xr,{children:[t.jsx(q,{children:"Edit Maintenance Template"}),t.jsx(en,{children:t.jsxs(Au,{children:[t.jsx(Se,{type:"error",children:"Template not found."}),t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{children:"Back to Maintenance"})})]})})]})]});const j=m.isPending||d.isPending;return t.jsxs(Zr,{children:[t.jsxs(Me,{children:[t.jsx(E,{label:"Mode",value:n?"EDIT":"CREATE"}),t.jsx(E,{label:"Boats Available",value:i.length.toString()}),n&&l&&t.jsx(E,{label:"Template Status",value:l.isActive?"ACTIVE":"INACTIVE"})]}),t.jsxs(Xr,{children:[t.jsx(q,{children:n?"Edit Maintenance Template":"Create Maintenance Template"}),t.jsx(en,{children:t.jsxs(wu,{onSubmit:h,children:[t.jsxs(Vt,{children:[t.jsx(Kt,{children:"Basic Information"}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Boat *"}),t.jsxs(To,{value:o.boatId,onChange:f=>y("boatId",f.target.value),required:!0,children:[t.jsx("option",{value:"",children:"Select a boat"}),i.map(f=>t.jsx("option",{value:f.id,children:f.name},f.id))]})]}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Title *"}),t.jsx(At,{type:"text",value:o.title,onChange:f=>y("title",f.target.value),placeholder:"e.g., Oil Change, Hull Cleaning, Engine Service",required:!0})]}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Component"}),t.jsx(At,{type:"text",value:o.component,onChange:f=>y("component",f.target.value),placeholder:"e.g., Engine, Hull, Electrical, Plumbing"})]}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Description"}),t.jsx(Cu,{value:o.description,onChange:f=>y("description",f.target.value),placeholder:"Detailed description of the maintenance task, including any special instructions or requirements"})]})]}),t.jsxs(Vt,{children:[t.jsx(Kt,{children:"Schedule"}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Recurring Task"}),t.jsxs("div",{style:{display:"flex",alignItems:"center"},children:[t.jsx(Ao,{type:"checkbox",checked:o.hasRecurrence,onChange:f=>y("hasRecurrence",f.target.checked)}),t.jsx("span",{children:"This is a recurring maintenance task"})]})]}),o.hasRecurrence&&t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Recurrence"}),t.jsxs(Su,{children:[t.jsx("span",{children:"Every"}),t.jsx(At,{type:"number",min:"1",value:o.recurrenceInterval,onChange:f=>y("recurrenceInterval",f.target.value),style:{width:"80px",flex:"none"}}),t.jsxs(To,{value:o.recurrenceType,onChange:f=>y("recurrenceType",f.target.value),style:{flex:"none",minWidth:"120px"},children:[t.jsx("option",{value:"days",children:"Days"}),t.jsx("option",{value:"weeks",children:"Weeks"}),t.jsx("option",{value:"months",children:"Months"}),t.jsx("option",{value:"years",children:"Years"}),t.jsx("option",{value:"engine_hours",children:"Engine Hours"})]})]})]})]}),t.jsxs(Vt,{children:[t.jsx(Kt,{children:"Estimates"}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Estimated Cost ($)"}),t.jsx(At,{type:"number",step:"0.01",min:"0",value:o.estimatedCost,onChange:f=>y("estimatedCost",f.target.value),placeholder:"0.00"})]}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Estimated Time (minutes)"}),t.jsx(At,{type:"number",min:"0",value:o.estimatedTime,onChange:f=>y("estimatedTime",f.target.value),placeholder:"60"})]})]}),n&&t.jsxs(Vt,{children:[t.jsx(Kt,{children:"Status"}),t.jsxs(Oe,{children:[t.jsx(Ue,{children:"Template Status"}),t.jsxs("div",{style:{display:"flex",alignItems:"center"},children:[t.jsx(Ao,{type:"checkbox",checked:o.isActive,onChange:f=>y("isActive",f.target.checked)}),t.jsx("span",{children:"Template is active (generates future events)"})]})]})]}),t.jsxs(ku,{children:[t.jsx(oe,{to:"/maintenance",children:t.jsx(k,{type:"button",children:"Cancel"})}),t.jsx(Q,{children:t.jsx(k,{type:"submit",disabled:j,variant:"accent",children:j?"Saving...":n?"Update Template":"Create Template"})})]})]})})]})]})}const Oa="nautical_settings",Fu=()=>{try{const e=localStorage.getItem(Oa);return e?JSON.parse(e):{}}catch{return{}}},Eu=e=>{localStorage.setItem(Oa,JSON.stringify(e))},lr=()=>{const[e,r]=b.useState(Fu);b.useEffect(()=>{Eu(e)},[e]);const n=b.useCallback(c=>e[c]||{enabled:!1},[e]),o=b.useCallback(c=>{var m;return((m=e[c])==null?void 0:m.enabled)??!1},[e]),s=b.useCallback(c=>{r(m=>{const d=m[c]||{enabled:!1};return{...m,[c]:{...d,enabled:!d.enabled}}})},[]),i=b.useCallback((c,m)=>{r(d=>{const h=d[c]||{enabled:!1};return{...d,[c]:{...h,apiKey:m}}})},[]),l=b.useCallback((c,m,d)=>{r(h=>{const y=h[c]||{enabled:!1};return{...h,[c]:{...y,options:{...y.options,[m]:d}}}})},[]);return{settings:e,getProviderConfig:n,isEnabled:o,toggleProvider:s,setApiKey:i,setProviderOption:l}},Lu={openseamap:{id:"openseamap",url:"https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png",attribution:'&copy; <a href="https://www.openseamap.org">OpenSeaMap</a> contributors',opacity:.7,maxZoom:18,type:"xyz"},"noaa-charts":{id:"noaa-charts",url:"https://tileservice.charts.noaa.gov/tiles/50000_1/{z}/{x}/{y}.png",attribution:'&copy; <a href="https://charts.noaa.gov">NOAA</a>',opacity:.8,maxZoom:16,type:"xyz"},gebco:{id:"gebco",url:"https://wms.gebco.net/mapserv?",attribution:'&copy; <a href="https://www.gebco.net">GEBCO</a>',opacity:.5,maxZoom:12,type:"wms",wmsLayers:"GEBCO_LATEST",wmsFormat:"image/png"},windy:{id:"windy",url:"https://tiles.windy.com/tiles/v10.0/wind/{z}/{x}/{y}.png",attribution:'&copy; <a href="https://windy.com">Windy</a>',opacity:.6,maxZoom:18,type:"xyz"},navionics:{id:"navionics",url:"https://backend.navionics.com/tile/{z}/{x}/{y}",attribution:'&copy; <a href="https://www.navionics.com">Navionics/Garmin</a>',opacity:.8,maxZoom:18,type:"xyz"}},cr=[{id:"openseamap",name:"OpenSeaMap",tier:"free",type:"tile",description:"Nautical marks, buoys, lights, and other seamark overlays on OpenStreetMap.",website:"https://openseamap.org",pros:["Completely free","Community maintained","Global coverage"],cons:["Limited detail in some regions","Community-dependent updates"],requiresApiKey:!1},{id:"noaa-charts",name:"NOAA Charts",tier:"free",type:"tile",description:"Official US nautical charts from NOAA via WMTS tile service.",website:"https://charts.noaa.gov",pros:["Official government data","High accuracy for US waters","Free to use"],cons:["US waters only","Can be slow to update"],requiresApiKey:!1},{id:"gebco",name:"GEBCO Bathymetry",tier:"free",type:"tile",description:"Global bathymetry and ocean depth visualization via WMS.",website:"https://www.gebco.net",pros:["Global ocean depth data","Free to use","Scientific quality"],cons:["Lower resolution in some areas","WMS can be slower than tile sources"],requiresApiKey:!1},{id:"noaa-coops",name:"NOAA CO-OPS",tier:"free",type:"data",description:"Real-time and predicted tide and current data from US stations.",website:"https://tidesandcurrents.noaa.gov",pros:["Official NOAA data","Real-time observations","Tide predictions"],cons:["US stations only","Rate limited"],requiresApiKey:!1},{id:"aisstream",name:"AISstream.io",tier:"free",type:"data",description:"Real-time coastal AIS vessel tracking via WebSocket.",website:"https://aisstream.io",pros:["Real-time vessel positions","WebSocket streaming","Free tier available"],cons:["Requires free API key","Coastal coverage only"],requiresApiKey:!0,apiKeySignupUrl:"https://aisstream.io/authenticate"},{id:"open-meteo",name:"Open-Meteo Marine",tier:"free",type:"data",description:"Marine weather forecasts including wave height, swell, and wind.",website:"https://open-meteo.com",pros:["Completely free","No API key needed","Global coverage"],cons:["Forecast only, no observations","Less detail than paid alternatives"],requiresApiKey:!1},{id:"worldtides",name:"WorldTides",tier:"paid",type:"data",description:"Global tide predictions and observations with high accuracy.",website:"https://www.worldtides.info",pros:["Global coverage","High accuracy","Detailed predictions"],cons:["Paid per request","Credits expire"],requiresApiKey:!0,apiKeySignupUrl:"https://www.worldtides.info/developer",pricingNote:"$10 for 5,000 predictions"},{id:"stormglass",name:"Stormglass",tier:"paid",type:"data",description:"Premium marine weather data from multiple sources.",website:"https://stormglass.io",pros:["Multiple weather models","High accuracy","Free tier (10 req/day)"],cons:["Limited free tier","Can be expensive at scale"],requiresApiKey:!0,apiKeySignupUrl:"https://stormglass.io/register",pricingNote:"Free tier: 10 requests/day. Paid plans from $19/month."},{id:"windy",name:"Windy",tier:"paid",type:"tile",description:"Animated wind, wave, and weather tile overlays.",website:"https://api.windy.com",pros:["Beautiful visualizations","Animated overlays","Multiple data layers"],cons:["Expensive","API key required"],requiresApiKey:!0,apiKeySignupUrl:"https://api.windy.com/signup",pricingNote:"~$720/year"},{id:"navionics",name:"Navionics/Garmin",tier:"paid",type:"tile",description:"Premium nautical charts with detailed depth contours and marina info.",website:"https://www.navionics.com",pros:["Industry-leading charts","Detailed depth data","Marina information"],cons:["Expensive","Contact for pricing","Complex integration"],requiresApiKey:!0,apiKeySignupUrl:"https://developer.navionics.com",pricingNote:"Contact Garmin/Navionics for pricing"},{id:"marinetraffic",name:"MarineTraffic",tier:"paid",type:"data",description:"Global vessel tracking with satellite AIS coverage.",website:"https://www.marinetraffic.com",pros:["Global coverage","Satellite + terrestrial AIS","Historical data"],cons:["Credit-based pricing","Can be expensive"],requiresApiKey:!0,apiKeySignupUrl:"https://www.marinetraffic.com/en/ais-api-services",pricingNote:"Credit-based pricing, varies by endpoint"}],zu=cr.filter(e=>e.tier==="free"),Du=cr.filter(e=>e.tier==="paid"),Iu=()=>{const{isEnabled:e,getProviderConfig:r}=lr();return{enabledTileLayers:b.useMemo(()=>cr.filter(o=>o.type==="tile"&&e(o.id)).filter(o=>o.requiresApiKey?!!r(o.id).apiKey:!0).map(o=>Lu[o.id]).filter(o=>!!o),[e,r])}},Mu="https://api.tidesandcurrents.noaa.gov/api/prod/datagetter",Ru=async(e,r,n,o)=>{try{const s=await fetch("https://api.tidesandcurrents.noaa.gov/mdapi/prod/webapi/stations.json?type=tidepredictions");return s.ok?((await s.json()).stations||[]).filter(c=>c.lat>=e&&c.lat<=n&&c.lng>=r&&c.lng<=o).map(c=>({id:c.id,name:c.name,latitude:c.lat,longitude:c.lng})):[]}catch{return[]}},Nu=async e=>{try{const r=new Date,n=new Date(r);n.setDate(n.getDate()+1);const o=r.toISOString().slice(0,10).replace(/-/g,""),s=n.toISOString().slice(0,10).replace(/-/g,""),i=new URLSearchParams({begin_date:o,end_date:s,station:e,product:"predictions",datum:"MLLW",time_zone:"lst_ldt",units:"english",format:"json",interval:"hilo"}),l=await fetch(`${Mu}?${i}`);return l.ok?((await l.json()).predictions||[]).map(m=>({time:m.t,value:parseFloat(m.v),type:m.type})):[]}catch{return[]}};class Pu{ws=null;vessels=new Map;callback=null;connect(r,n,o){this.callback=o,this.disconnect(),this.ws=new WebSocket("wss://stream.aisstream.io/v0/stream"),this.ws.onopen=()=>{var s;(s=this.ws)==null||s.send(JSON.stringify({APIKey:r,BoundingBoxes:[n]}))},this.ws.onmessage=s=>{var i,l,c;try{const m=JSON.parse(s.data);if((i=m.Message)!=null&&i.PositionReport){const d=m.Message.PositionReport,h=m.MetaData,y={mmsi:h.MMSI,name:((l=h.ShipName)==null?void 0:l.trim())||`MMSI ${h.MMSI}`,latitude:d.Latitude,longitude:d.Longitude,heading:d.TrueHeading??d.Cog??0,speed:d.Sog??0,shipType:h.ShipType??0,timestamp:Date.now()};this.vessels.set(y.mmsi,y);const j=Date.now()-6e5;for(const[f,p]of this.vessels)p.timestamp<j&&this.vessels.delete(f);(c=this.callback)==null||c.call(this,Array.from(this.vessels.values()))}}catch{}},this.ws.onerror=()=>{setTimeout(()=>{this.callback&&this.connect(r,n,this.callback)},5e3)},this.ws.onclose=()=>{}}disconnect(){var r;(r=this.ws)==null||r.close(),this.ws=null,this.vessels.clear()}getVessels(){return Array.from(this.vessels.values())}}const Bu=async(e,r)=>{var n,o,s;try{const i=new URLSearchParams({latitude:e.toString(),longitude:r.toString(),current:["wave_height","wave_period","wave_direction","wind_wave_height","wind_wave_period","swell_wave_height","swell_wave_period"].join(","),hourly:"temperature_2m,wind_speed_10m,wind_direction_10m",forecast_days:"1",timezone:"auto"}),l=await fetch(`https://marine-api.open-meteo.com/v1/marine?${i}`);if(!l.ok)return null;const c=await l.json(),m=c.current||{},d=c.hourly||{};return{latitude:c.latitude,longitude:c.longitude,waveHeight:m.wave_height??null,wavePeriod:m.wave_period??null,waveDirection:m.wave_direction??null,windSpeed:((n=d.wind_speed_10m)==null?void 0:n[0])??null,windDirection:((o=d.wind_direction_10m)==null?void 0:o[0])??null,swellHeight:m.swell_wave_height??null,swellPeriod:m.swell_wave_period??null,temperature:((s=d.temperature_2m)==null?void 0:s[0])??null,timestamp:m.time||new Date().toISOString()}}catch{return null}},Ou=async(e,r,n)=>{try{const o=await fetch(`https://www.worldtides.info/api/v3?extremes&lat=${e}&lon=${r}&key=${n}`);return o.ok?((await o.json()).extremes||[]).map(i=>({date:i.date,height:i.height,type:i.type==="High"?"High":"Low"})):[]}catch{return[]}},Uu=async(e,r,n)=>{var o;try{const s=["waveHeight","wavePeriod","waveDirection","windSpeed","windDirection","waterTemperature","airTemperature","visibility"].join(","),i=await fetch(`https://api.stormglass.io/v2/weather/point?lat=${e}&lng=${r}&params=${s}`,{headers:{Authorization:n}});if(!i.ok)return null;const c=(o=(await i.json()).hours)==null?void 0:o[0];if(!c)return null;const m=d=>{var h,y;return((h=c[d])==null?void 0:h.sg)??((y=c[d])==null?void 0:y.noaa)??null};return{waveHeight:m("waveHeight"),wavePeriod:m("wavePeriod"),waveDirection:m("waveDirection"),windSpeed:m("windSpeed"),windDirection:m("windDirection"),waterTemperature:m("waterTemperature"),airTemperature:m("airTemperature"),visibility:m("visibility"),timestamp:c.time}}catch{return null}},qu=async(e,r,n,o,s)=>{try{const i=await fetch(`https://services.marinetraffic.com/api/exportvessels/v:8/${s}/MINLAT:${e}/MAXLAT:${n}/MINLON:${r}/MAXLON:${o}/protocol:jsono`);if(!i.ok)return[];const l=await i.json();return(Array.isArray(l)?l:[]).map(c=>({mmsi:parseInt(c.MMSI),name:c.SHIPNAME||`MMSI ${c.MMSI}`,latitude:parseFloat(c.LAT),longitude:parseFloat(c.LON),speed:parseFloat(c.SPEED)/10,heading:parseInt(c.HEADING),shipType:c.SHIPTYPE||"",destination:c.DESTINATION||"",timestamp:c.TIMESTAMP||""}))}catch{return[]}},Hu=e=>{const{isEnabled:r,getProviderConfig:n}=lr(),[o,s]=b.useState([]),[i,l]=b.useState([]),[c,m]=b.useState([]),[d,h]=b.useState([]),[y,j]=b.useState(null),[f,p]=b.useState(null),[g]=b.useState(!1),u=b.useRef(null);return b.useEffect(()=>{if(!e||!r("noaa-coops")){m([]);return}const x=async()=>{const $=await Ru(e.minLat,e.minLng,e.maxLat,e.maxLng),w=await Promise.all($.slice(0,20).map(async A=>{const T=await Nu(A.id);return{...A,predictions:T}}));m(w)};x();const v=setInterval(x,30*60*1e3);return()=>clearInterval(v)},[e==null?void 0:e.minLat,e==null?void 0:e.maxLat,e==null?void 0:e.minLng,e==null?void 0:e.maxLng,r]),b.useEffect(()=>{var $;const x=n("aisstream");if(!e||!r("aisstream")||!x.apiKey){($=u.current)==null||$.disconnect(),u.current=null,s([]);return}const v=new Pu;return u.current=v,v.connect(x.apiKey,[[e.minLat,e.minLng],[e.maxLat,e.maxLng]],w=>s(w)),()=>{v.disconnect(),u.current=null}},[e==null?void 0:e.minLat,e==null?void 0:e.maxLat,r,n]),b.useEffect(()=>{if(!e||!r("open-meteo")){j(null);return}const x=async()=>{const $=await Bu(e.centerLat,e.centerLng);j($)};x();const v=setInterval(x,15*60*1e3);return()=>clearInterval(v)},[e==null?void 0:e.centerLat,e==null?void 0:e.centerLng,r]),b.useEffect(()=>{const x=n("worldtides");if(!e||!r("worldtides")||!x.apiKey){h([]);return}const v=async()=>{const w=await Ou(e.centerLat,e.centerLng,x.apiKey);h(w)};v();const $=setInterval(v,30*60*1e3);return()=>clearInterval($)},[e==null?void 0:e.centerLat,e==null?void 0:e.centerLng,r,n]),b.useEffect(()=>{const x=n("stormglass");if(!e||!r("stormglass")||!x.apiKey){p(null);return}const v=async()=>{const w=await Uu(e.centerLat,e.centerLng,x.apiKey);p(w)};v();const $=setInterval(v,15*60*1e3);return()=>clearInterval($)},[e==null?void 0:e.centerLat,e==null?void 0:e.centerLng,r,n]),b.useEffect(()=>{const x=n("marinetraffic");if(!e||!r("marinetraffic")||!x.apiKey){l([]);return}const v=async()=>{const w=await qu(e.minLat,e.minLng,e.maxLat,e.maxLng,x.apiKey);l(w)};v();const $=setInterval(v,5*60*1e3);return()=>clearInterval($)},[e==null?void 0:e.minLat,e==null?void 0:e.maxLat,r,n]),{vessels:o,marineTrafficVessels:i,tideStations:c,worldTides:d,weather:y,stormglassWeather:f,isLoading:g}},Ve={all:["locations"],lists:()=>[...Ve.all,"list"],list:e=>[...Ve.lists(),{filters:e}],details:()=>[...Ve.all,"detail"],detail:e=>[...Ve.details(),e],nearby:(e,r,n)=>[...Ve.all,"nearby",{lat:e,lng:r,radius:n}]},Wu=e=>he({queryKey:Ve.list(e||{}),queryFn:()=>O.getMarkedLocations(e)}),Vu=()=>{const e=Y();return ee({mutationFn:r=>O.createMarkedLocation(r),onSuccess:()=>{e.invalidateQueries({queryKey:Ve.lists()})}})},Ku=()=>{const e=Y();return ee({mutationFn:r=>O.deleteMarkedLocation(r),onSuccess:()=>{e.invalidateQueries({queryKey:Ve.lists()})}})},Gu="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAApCAYAAADAk4LOAAAFgUlEQVR4Aa1XA5BjWRTN2oW17d3YaZtr2962HUzbDNpjszW24mRt28p47v7zq/bXZtrp/lWnXr337j3nPCe85NcypgSFdugCpW5YoDAMRaIMqRi6aKq5E3YqDQO3qAwjVWrD8Ncq/RBpykd8oZUb/kaJutow8r1aP9II0WmLKLIsJyv1w/kqw9Ch2MYdB++12Onxee/QMwvf4/Dk/Lfp/i4nxTXtOoQ4pW5Aj7wpici1A9erdAN2OH64x8OSP9j3Ft3b7aWkTg/Fm91siTra0f9on5sQr9INejH6CUUUpavjFNq1B+Oadhxmnfa8RfEmN8VNAsQhPqF55xHkMzz3jSmChWU6f7/XZKNH+9+hBLOHYozuKQPxyMPUKkrX/K0uWnfFaJGS1QPRtZsOPtr3NsW0uyh6NNCOkU3Yz+bXbT3I8G3xE5EXLXtCXbbqwCO9zPQYPRTZ5vIDXD7U+w7rFDEoUUf7ibHIR4y6bLVPXrz8JVZEql13trxwue/uDivd3fkWRbS6/IA2bID4uk0UpF1N8qLlbBlXs4Ee7HLTfV1j54APvODnSfOWBqtKVvjgLKzF5YdEk5ewRkGlK0i33Eofffc7HT56jD7/6U+qH3Cx7SBLNntH5YIPvODnyfIXZYRVDPqgHtLs5ABHD3YzLuespb7t79FY34DjMwrVrcTuwlT55YMPvOBnRrJ4VXTdNnYug5ucHLBjEpt30701A3Ts+HEa73u6dT3FNWwflY86eMHPk+Yu+i6pzUpRrW7SNDg5JHR4KapmM5Wv2E8Tfcb1HoqqHMHU+uWDD7zg54mz5/2BSnizi9T1Dg4QQXLToGNCkb6tb1NU+QAlGr1++eADrzhn/u8Q2YZhQVlZ5+CAOtqfbhmaUCS1ezNFVm2imDbPmPng5wmz+gwh+oHDce0eUtQ6OGDIyR0uUhUsoO3vfDmmgOezH0mZN59x7MBi++WDL1g/eEiU3avlidO671bkLfwbw5XV2P8Pzo0ydy4t2/0eu33xYSOMOD8hTf4CrBtGMSoXfPLchX+J0ruSePw3LZeK0juPJbYzrhkH0io7B3k164hiGvawhOKMLkrQLyVpZg8rHFW7E2uHOL888IBPlNZ1FPzstSJM694fWr6RwpvcJK60+0HCILTBzZLFNdtAzJaohze60T8qBzyh5ZuOg5e7uwQppofEmf2++DYvmySqGBuKaicF1blQjhuHdvCIMvp8whTTfZzI7RldpwtSzL+F1+wkdZ2TBOW2gIF88PBTzD/gpeREAMEbxnJcaJHNHrpzji0gQCS6hdkEeYt9DF/2qPcEC8RM28Hwmr3sdNyht00byAut2k3gufWNtgtOEOFGUwcXWNDbdNbpgBGxEvKkOQsxivJx33iow0Vw5S6SVTrpVq11ysA2Rp7gTfPfktc6zhtXBBC+adRLshf6sG2RfHPZ5EAc4sVZ83yCN00Fk/4kggu40ZTvIEm5g24qtU4KjBrx/BTTH8ifVASAG7gKrnWxJDcU7x8X6Ecczhm3o6YicvsLXWfh3Ch1W0k8x0nXF+0fFxgt4phz8QvypiwCCFKMqXCnqXExjq10beH+UUA7+nG6mdG/Pu0f3LgFcGrl2s0kNNjpmoJ9o4B29CMO8dMT4Q5ox8uitF6fqsrJOr8qnwNbRzv6hSnG5wP+64C7h9lp30hKNtKdWjtdkbuPA19nJ7Tz3zR/ibgARbhb4AlhavcBebmTHcFl2fvYEnW0ox9xMxKBS8btJ+KiEbq9zA4RthQXDhPa0T9TEe69gWupwc6uBUphquXgf+/FrIjweHQS4/pduMe5ERUMHUd9xv8ZR98CxkS4F2n3EUrUZ10EYNw7BWm9x1GiPssi3GgiGRDKWRYZfXlON+dfNbM+GgIwYdwAAAAASUVORK5CYII=",_u="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAABSCAMAAAAhFXfZAAAC91BMVEVMaXEzeak2f7I4g7g3g7cua5gzeKg8hJo3grY4g7c3grU0gLI2frE0daAubJc2gbQwd6QzeKk2gLMtd5sxdKIua5g1frA2f7IydaM0e6w2fq41fK01eqo3grgubJgta5cxdKI1f7AydaQydaMxc6EubJgvbJkwcZ4ubZkwcJwubZgubJcydqUydKIxapgubJctbJcubZcubJcvbJYubJcvbZkubJctbJctbZcubJg2f7AubJcrbZcubJcubJcua5g3grY0fq8ubJcubJdEkdEwhsw6i88vhswuhcsuhMtBjMgthMsrg8srgss6is8qgcs8i9A9iMYtg8spgcoogMo7hcMngMonf8olfso4gr8kfck5iM8jfMk4iM8he8k1fro7itAgesk2hs8eecgzfLcofssdeMg0hc4cd8g2hcsxeLQbdsgZdcgxeLImfcszhM0vda4xgckzhM4xg84wf8Yxgs4udKsvfcQucqhUndROmdM1fK0wcZ8vb5w0eqpQm9MzeKhXoNVcpdYydKNWn9VZotVKltJFjsIwcJ1Rms9OlslLmtH///8+kc9epdYzd6dbo9VHkMM2f7FHmNBClM8ydqVcpNY9hro3gLM9hLczealQmcw3fa46f7A8gLMxc6I3eagyc6FIldJMl9JSnNRSntNNl9JPnNJFi75UnM9ZodVKksg8kM45jc09e6ZHltFBk883gbRBh7pDk9EwcaBzn784g7dKkcY2i81Om9M7j85Llc81is09g7Q4grY/j9A0eqxKmdFFltBEjcXf6fFImdBCiLxJl9FGlNFBi78yiMxVndEvbpo6js74+vx+psPP3+o/ks5HkcpGmNCjwdZCkNDM3ehYoNJEls+lxNkxh8xHks0+jdC1zd5Lg6r+/v/H2ufz9/o3jM3t8/edvdM/k89Th61OiLBSjbZklbaTt9BfptdjmL1AicBHj8hGk9FAgK1dkLNTjLRekrdClc/k7fM0icy0y9tgp9c4jc2NtM9Dlc8zicxeXZn3AAAAQ3RSTlMAHDdTb4yPA+LtnEQmC4L2EmHqB7XA0d0sr478x4/Yd5i1zOfyPkf1sLVq4Nh3FvjxopQ2/STNuFzUwFIwxKaejILpIBEV9wAABhVJREFUeF6s1NdyFEcYBeBeoQIhRAkLlRDGrhIgY3BJL8CVeKzuyXFzzjkn5ZxzzuScg3PO8cKzu70JkO0LfxdTU//pM9vTu7Xgf6KqOVTb9X7toRrVEfBf1HTVjZccrT/2by1VV928Yty9ZbVuucdz90frG8DBjl9pVApbOstvmMuvVgaNXSfAAd6pGxpy6yxf5ph43pS/4f3uoaGm2rdu72S9xzOvMymkZFq/ptDrk90mhW7e4zl7HLzhxGWPR20xmSxJ/VqldG5m9XhaVOA1DadsNh3Pu5L2N6QtPO/32JpqQBVVk20oy/Pi2s23WEvyfHbe1thadVQttvm7Llf65gGmXK67XtupyoM7HQhmXdLS8oGWJNeOJ3C5fG5XCEJnkez3/oFdsvgJ4l2ANZwhrJKk/7OSXa+3Vw2WJMlKnGkobouYk6T0TyX30klOUnTD9HJ5qpckL3EW/w4XF3Xd0FGywXUrstrclVsqz5Pd/sXFYyDnPdrLcQODmGOK47IZb4CmibmMn+MYRzFZ5jg33ZL/EJrWcszHmANy3ARBK/IXtciJy8VsitPSdE3uuHxzougojcUdr8/32atnz/ev3f/K5wtpxUTpcaI45zusVDpYtZi+jg0oU9b3x74h7+n9ABvYEZeKaVq0sh0AtLKsFtqNBdeT0MrSzwwlq9+x6xAO4tgOtSzbCjrNQQiNvQUbUEubvzBUeGw26yDCsRHCoLkTHDa7IdOLIThs/gHvChszh2CimE8peRs47cxANI0lYNB5y1DljpOF0IhzBDPOZnDOqYYbeGKECbPzWnXludPphw5c2YBq5zlwXphIbO4VDCZ0gnPfUO1TwZoYwAs2ExPCedAu9DAjfQUjzITQb3jNj0KG2Sgt6BHaQUdYzWz+XmBktOHwanXjaSTcwwziBcuMOtwBmqPrTOxFQR/DRKKPqyur0aiW6cULYsx6tBm0jXpR/AUWR6HRq9WVW6MRhIq5jLyjbaCTDCijyYJNpCajdyobP/eTw0iexBAKkJ3gA5KcQb2zBXsIBckn+xVv8jkZSaEFHE+jFEleAEfayRU0MouNoBmB/L50Ai/HSLIHxcrpCvnhSQAuakKp2C/YbCylJjXRVy/z3+Kv/RrNcCo+WUzlVEhzKffnTQnxeN9fWF88fiNCUdSTsaufaChKWInHeysygfpIqagoakW+vV20J8uyl6TyNKEZWV4oRSPyCkWpgOLSbkCObT8o2r6tlG58HQquf6O0v50tB7JM7F4EORd2dx/K0w/KHsVkLPaoYrwgP/y7krr3SSMA4zj+OBgmjYkxcdIJQyQRKgg2viX9Hddi9UBb29LrKR7CVVEEEXWojUkXNyfTNDE14W9gbHJNuhjDettN3ZvbOvdOqCD3Jp/9l+/wJE+9PkYGjx/fqkys3S2rMozM/o2106rfMUINo6hVqz+eu/hd1c4xTg0TAfy5kV+4UG6+IthHTU9woWmxuKNbTfuCSfovBCxq7EtHqvYL4Sm6F8GVxsSXHMQ07TOi1DKtZxjWaaIyi4CXWjxPccUw8WVbMYY5wxC1mzEyXMJWkllpRloi+Kkoq69sxBTlElF6aAxYUbjXNlhlDZilDnM4U5SlN5biRsRHnbx3mbeWjEh4mEyiuJDl5XcWVmX5GvNkFgLWZM5qwsop4/AWfLhU1cR7k1VVvcYCWRkOI6Xy5gmnphCYIkvzuNYzHzosq2oNk2RtSs8khfUOfHIDgR6ysYBaMpl4uEgk2U/oJTs9AaTSwma7dT69geAE2ZpEjUsn2ieJNHeKfrI3EcAGJ2ZaNgVuC8EBctCLc57P5u5led6IOBkIYkuQMrmmjChs4VkfOerHqSBkPzZlhe06RslZ3zMjk2sscqKwY0RcjKK+LWbzd7KiHhkncs/siFJ+V5eXxD34B8nVuJEpGJNmxN2gH3vSvp7J70tF+D1Ej8qUJD1TkErAND2GZwTFg/LubvmgiBG3SOvdlsqFQrkEzJCL1rstlnVFROixZoDDSuXQFHESwVGlcuQcMb/b42NgjLowh5MTDFE3vNB5qStRIErdCQEh6pLPR92anSUb/wAIhldAaDMpGgAAAABJRU5ErkJggg==",Qu="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACkAAAApCAQAAAACach9AAACMUlEQVR4Ae3ShY7jQBAE0Aoz/f9/HTMzhg1zrdKUrJbdx+Kd2nD8VNudfsL/Th///dyQN2TH6f3y/BGpC379rV+S+qqetBOxImNQXL8JCAr2V4iMQXHGNJxeCfZXhSRBcQMfvkOWUdtfzlLgAENmZDcmo2TVmt8OSM2eXxBp3DjHSMFutqS7SbmemzBiR+xpKCNUIRkdkkYxhAkyGoBvyQFEJEefwSmmvBfJuJ6aKqKWnAkvGZOaZXTUgFqYULWNSHUckZuR1HIIimUExutRxwzOLROIG4vKmCKQt364mIlhSyzAf1m9lHZHJZrlAOMMztRRiKimp/rpdJDc9Awry5xTZCte7FHtuS8wJgeYGrex28xNTd086Dik7vUMscQOa8y4DoGtCCSkAKlNwpgNtphjrC6MIHUkR6YWxxs6Sc5xqn222mmCRFzIt8lEdKx+ikCtg91qS2WpwVfBelJCiQJwvzixfI9cxZQWgiSJelKnwBElKYtDOb2MFbhmUigbReQBV0Cg4+qMXSxXSyGUn4UbF8l+7qdSGnTC0XLCmahIgUHLhLOhpVCtw4CzYXvLQWQbJNmxoCsOKAxSgBJno75avolkRw8iIAFcsdc02e9iyCd8tHwmeSSoKTowIgvscSGZUOA7PuCN5b2BX9mQM7S0wYhMNU74zgsPBj3HU7wguAfnxxjFQGBE6pwN+GjME9zHY7zGp8wVxMShYX9NXvEWD3HbwJf4giO4CFIQxXScH1/TM+04kkBiAAAAAElFTkSuQmCC";delete Ie.Icon.Default.prototype._getIconUrl;Ie.Icon.Default.mergeOptions({iconRetinaUrl:_u,iconUrl:Gu,shadowUrl:Qu});const Ju=a.div`
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px); // Account for header and footer
  gap: ${e=>e.theme.spacing.md};
`,Yu=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.md};
`,Zu=a.div`
  position: relative;
  flex: 1;
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  min-height: 600px;
`,Xu=a(L)`
  flex: 1;
  
  .leaflet-container {
    height: 100%;
    min-height: 500px;
    background-color: ${e=>e.theme.colors.surface.dark};
  }
  
  .leaflet-control-container {
    .leaflet-control {
      background-color: ${e=>e.theme.colors.surface.medium};
      border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};

      a {
        color: ${e=>e.theme.colors.text.primary};
        background-color: ${e=>e.theme.colors.surface.medium};

        &:hover {
          background-color: ${e=>e.theme.colors.primary.neonCarrot};
          color: ${e=>e.theme.colors.text.inverse};
        }
      }
    }
  }
`,eg=a(L)`
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
`,tg=a.div`
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,rg=a.div`
  padding: ${e=>e.theme.spacing.sm};
  background-color: ${e=>e.theme.colors.surface.medium};
  border-radius: ${e=>e.theme.borderRadius.sm};
  border-left: 3px solid ${e=>e.theme.colors.primary.anakiwa};

  .location-name {
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    color: ${e=>e.theme.colors.primary.anakiwa};
    margin-bottom: ${e=>e.theme.spacing.xs};
  }
  
  .location-category {
    font-size: ${e=>e.theme.typography.fontSize.sm};
    color: ${e=>e.theme.colors.text.secondary};
    text-transform: uppercase;
    margin-bottom: ${e=>e.theme.spacing.xs};
  }
  
  .location-notes {
    font-size: ${e=>e.theme.typography.fontSize.sm};
    color: ${e=>e.theme.colors.text.muted};
  }
  
  .location-actions {
    margin-top: ${e=>e.theme.spacing.sm};
    display: flex;
    gap: ${e=>e.theme.spacing.sm};
  }
`,ng=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
  
  input, select, textarea {
    padding: ${e=>e.theme.spacing.sm};
    background-color: ${e=>e.theme.colors.surface.medium};
    border: 1px solid ${e=>e.theme.colors.primary.neonCarrot};
    border-radius: ${e=>e.theme.borderRadius.sm};
    color: ${e=>e.theme.colors.text.primary};
    font-family: ${e=>e.theme.typography.fontFamily.primary};

    &:focus {
      outline: none;
      border-color: ${e=>e.theme.colors.primary.tanoi};
      box-shadow: 0 0 0 2px ${e=>e.theme.colors.primary.neonCarrot}20;
    }
  }
  
  textarea {
    resize: vertical;
    min-height: 60px;
  }
`,og=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.sm};
  flex-wrap: wrap;
  align-items: center;
  
  label {
    color: ${e=>e.theme.colors.text.secondary};
    font-size: ${e=>e.theme.typography.fontSize.sm};
    text-transform: uppercase;
  }
  
  select {
    padding: ${e=>e.theme.spacing.xs} ${e=>e.theme.spacing.sm};
    background-color: ${e=>e.theme.colors.surface.medium};
    border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
    border-radius: ${e=>e.theme.borderRadius.sm};
    color: ${e=>e.theme.colors.text.primary};
    font-family: ${e=>e.theme.typography.fontFamily.primary};
  }
`,ag=e=>{const r={fishing:"#66FF66",marina:"#6688CC",anchorage:"#FFFF66",hazard:"#FF6666",other:"#CC99CC"};return new Ie.DivIcon({html:`<div style="
      background-color: ${r[e]||r.other};
      width: 20px;
      height: 20px;
      border-radius: 50%;
      border: 2px solid #000;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 10px;
      color: #000;
    ">${e.charAt(0).toUpperCase()}</div>`,className:"custom-marker",iconSize:[20,20],iconAnchor:[10,10]})},sg=({onMapClick:e})=>(Jo({click:r=>{e(r.latlng.lat,r.latlng.lng)}}),null),ig=({onBoundsChange:e})=>{const r=Jo({moveend:()=>{const n=r.getBounds(),o=r.getCenter();e({minLat:n.getSouth(),minLng:n.getWest(),maxLat:n.getNorth(),maxLng:n.getEast(),centerLat:o.lat,centerLng:o.lng})},zoomend:()=>{const n=r.getBounds(),o=r.getCenter();e({minLat:n.getSouth(),minLng:n.getWest(),maxLat:n.getNorth(),maxLng:n.getEast(),centerLat:o.lat,centerLng:o.lng})}});return null},lg=()=>{const[e,r]=b.useState(!0),[n,o]=b.useState(!0),[s,i]=b.useState(""),[l,c]=b.useState(!1),[m,d]=b.useState({name:"",category:"other",notes:"",latitude:null,longitude:null}),[h,y]=b.useState(null),j=b.useRef(null),{enabledTileLayers:f}=Iu(),[p,g]=b.useState(null),u=Hu(p),{isEnabled:x}=lr(),[v,$]=b.useState(new Set),w=b.useCallback(C=>{$(W=>{const K=new Set(W);return K.has(C)?K.delete(C):K.add(C),K})},[]),A=b.useCallback(C=>x(C)&&!v.has(C),[x,v]),T=cr.filter(C=>x(C.id)),{data:I=[],isLoading:D}=_e(),{data:U=[],isLoading:F}=Wu(s?{category:s}:void 0),R=Vu(),H=Ku(),G=Ke.useMemo(()=>{if(I.length>0){const C=I.flatMap(W=>W.gpsPoints);if(C.length>0){const W=C.reduce((le,ae)=>le+ae.latitude,0)/C.length,K=C.reduce((le,ae)=>le+ae.longitude,0)/C.length;return[W,K]}}return[37.7749,-122.4194]},[I]),J=b.useCallback((C,W)=>{console.log("Map clicked:",{lat:C,lng:W,isAddingLocation:l}),l&&d(K=>({...K,latitude:C,longitude:W}))},[l]),B=async()=>{if(console.log("handleCreateLocation called with:",m),!m.name){console.log("Validation failed: no name"),alert("Please enter a location name");return}if(m.latitude===null||m.longitude===null){console.log("Validation failed: no coordinates"),alert("Please click on the map to set coordinates");return}console.log("Validation passed, calling mutation...");try{const C=await R.mutateAsync({name:m.name,latitude:m.latitude,longitude:m.longitude,category:m.category,notes:m.notes||void 0});console.log("Location created successfully:",C),d({name:"",category:"other",notes:"",latitude:null,longitude:null}),c(!1)}catch(C){console.error("Failed to create location:",C),alert("Failed to save location. Please try again.")}},te=async C=>{if(window.confirm("Are you sure you want to delete this location?"))try{await H.mutateAsync(C),y(null)}catch(W){console.error("Failed to delete location:",W)}},be=()=>e?I.map(C=>{var ae,Te,qe;if(C.gpsPoints.length<2)return null;const W=C.gpsPoints.map(Ae=>[Ae.latitude,Ae.longitude]),K=W[0],le=W[W.length-1];return t.jsxs(Ke.Fragment,{children:[t.jsx(_o,{positions:W,color:"#FF9966",weight:3,opacity:.7}),t.jsx($e,{position:K,children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:"Trip Start"}),t.jsx("br",{}),new Date(C.startTime).toLocaleString(),t.jsx("br",{}),"Boat: ",C.boatId]})})}),t.jsx($e,{position:le,children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:"Trip End"}),t.jsx("br",{}),new Date(C.endTime).toLocaleString(),t.jsx("br",{}),"Duration: ",Math.round((((ae=C.statistics)==null?void 0:ae.durationSeconds)||0)/60)," minutes",t.jsx("br",{}),"Distance: ",((((Te=C.statistics)==null?void 0:Te.distanceMeters)||0)/1e3).toFixed(2)," km"]})})}),(((qe=C.statistics)==null?void 0:qe.stopPoints)||[]).map((Ae,dr)=>t.jsx($e,{position:[Ae.latitude,Ae.longitude],icon:new Ie.DivIcon({html:`<div style="
                  background-color: #FFFF66;
                  width: 16px;
                  height: 16px;
                  border-radius: 50%;
                  border: 2px solid #000;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-weight: bold;
                  font-size: 8px;
                  color: #000;
                ">S</div>`,className:"stop-marker",iconSize:[16,16],iconAnchor:[8,8]}),children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:"Stop Point"}),t.jsx("br",{}),"Duration: ",Math.round(Ae.durationSeconds/60)," minutes",t.jsx("br",{}),"From: ",new Date(Ae.startTime).toLocaleString(),t.jsx("br",{}),"To: ",new Date(Ae.endTime).toLocaleString()]})})},`${C.id}-stop-${dr}`))]},C.id)}):null,N=()=>n?U.map(C=>t.jsx($e,{position:[C.latitude,C.longitude],icon:ag(C.category),eventHandlers:{click:()=>y(C)},children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:C.name}),t.jsx("br",{}),"Category: ",C.category,t.jsx("br",{}),C.notes&&t.jsxs(t.Fragment,{children:["Notes: ",C.notes,t.jsx("br",{})]}),C.tags.length>0&&t.jsxs(t.Fragment,{children:["Tags: ",C.tags.join(", "),t.jsx("br",{})]}),t.jsxs("small",{children:["Created: ",new Date(C.createdAt).toLocaleDateString()]})]})})},C.id)):null;return t.jsxs(Ju,{children:[t.jsx(q,{children:"Navigation Chart"}),t.jsx(Yu,{children:t.jsxs(og,{children:[t.jsx("label",{children:"Display:"}),t.jsx(k,{variant:e?"primary":"secondary",size:"sm",onClick:()=>r(!e),children:"Trip Routes"}),t.jsx(k,{variant:n?"primary":"secondary",size:"sm",onClick:()=>o(!n),children:"Locations"}),T.length>0&&t.jsxs(t.Fragment,{children:[t.jsx("label",{children:"Overlays:"}),T.map(C=>t.jsx(k,{variant:v.has(C.id)?"secondary":"primary",size:"sm",onClick:()=>w(C.id),children:C.name},C.id))]}),t.jsx("label",{children:"Category:"}),t.jsxs("select",{value:s,onChange:C=>i(C.target.value),children:[t.jsx("option",{value:"",children:"All Categories"}),t.jsx("option",{value:"fishing",children:"Fishing"}),t.jsx("option",{value:"marina",children:"Marina"}),t.jsx("option",{value:"anchorage",children:"Anchorage"}),t.jsx("option",{value:"hazard",children:"Hazard"}),t.jsx("option",{value:"other",children:"Other"})]})]})}),t.jsxs(Zu,{children:[t.jsxs(Xu,{title:"Chart Display",padding:"none",children:[t.jsxs(Qo,{center:G,zoom:10,style:{height:"100%",width:"100%"},ref:j,children:[t.jsx(an,{attribution:'© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',url:"https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"}),f.filter(C=>!v.has(C.id)).map(C=>C.type==="wms"?t.jsx(ns,{url:C.url,layers:C.wmsLayers||"",format:C.wmsFormat||"image/png",transparent:!0,opacity:C.opacity,attribution:C.attribution},C.id):t.jsx(an,{url:C.url,opacity:C.opacity,maxZoom:C.maxZoom,attribution:C.attribution},C.id)),t.jsx(ig,{onBoundsChange:g}),t.jsx(sg,{onMapClick:J}),be(),N(),A("aisstream")&&u.vessels.map(C=>t.jsx($e,{position:[C.latitude,C.longitude],icon:new Ie.DivIcon({html:`<div style="
                    width: 0; height: 0;
                    border-left: 6px solid transparent;
                    border-right: 6px solid transparent;
                    border-bottom: 14px solid #00FFFF;
                    transform: rotate(${C.heading}deg);
                  "></div>`,className:"vessel-marker",iconSize:[12,14],iconAnchor:[6,7]}),children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:C.name}),t.jsx("br",{}),"MMSI: ",C.mmsi,t.jsx("br",{}),"Speed: ",C.speed.toFixed(1)," kts",t.jsx("br",{}),"Heading: ",C.heading,"°"]})})},`ais-${C.mmsi}`)),A("marinetraffic")&&u.marineTrafficVessels.map(C=>t.jsx($e,{position:[C.latitude,C.longitude],icon:new Ie.DivIcon({html:`<div style="
                    width: 0; height: 0;
                    border-left: 6px solid transparent;
                    border-right: 6px solid transparent;
                    border-bottom: 14px solid #FF00FF;
                    transform: rotate(${C.heading}deg);
                  "></div>`,className:"vessel-marker",iconSize:[12,14],iconAnchor:[6,7]}),children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:C.name}),t.jsx("br",{}),"MMSI: ",C.mmsi,t.jsx("br",{}),"Speed: ",C.speed.toFixed(1)," kts",t.jsx("br",{}),"Destination: ",C.destination]})})},`mt-${C.mmsi}`)),A("noaa-coops")&&u.tideStations.map(C=>t.jsx($e,{position:[C.latitude,C.longitude],icon:new Ie.DivIcon({html:`<div style="
                    background: #0066FF;
                    color: white;
                    width: 22px;
                    height: 22px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 10px;
                    font-weight: bold;
                    border: 2px solid white;
                  ">T</div>`,className:"tide-marker",iconSize:[22,22],iconAnchor:[11,11]}),children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:C.name}),t.jsx("br",{}),"Station: ",C.id,t.jsx("br",{}),C.predictions.length>0&&t.jsxs(t.Fragment,{children:[t.jsx("strong",{children:"Predictions:"}),t.jsx("br",{}),C.predictions.slice(0,6).map((W,K)=>t.jsxs("span",{children:[W.type==="H"?"▲ High":"▼ Low",": ",W.value.toFixed(1)," ft at ",W.time,t.jsx("br",{})]},K))]})]})})},`tide-${C.id}`)),l&&m.latitude!==null&&m.longitude!==null&&t.jsx($e,{position:[m.latitude,m.longitude],children:t.jsx(we,{children:t.jsxs("div",{children:[t.jsx("strong",{children:"New Location"}),t.jsx("br",{}),'Click "Save Location" to confirm']})})})]}),(A("open-meteo")||A("stormglass"))&&u.weather&&t.jsxs("div",{style:{position:"absolute",bottom:"10px",left:"10px",background:"rgba(0,0,0,0.85)",color:"#99CCFF",padding:"8px 12px",borderRadius:"4px",border:"1px solid #336699",fontSize:"12px",fontFamily:"monospace",zIndex:1e3,lineHeight:"1.5"},children:[t.jsx("div",{style:{fontWeight:"bold",marginBottom:"4px",color:"#FFCC99"},children:"MARINE WEATHER"}),u.weather.waveHeight!=null&&t.jsxs("div",{children:["Waves: ",u.weather.waveHeight,"m"]}),u.weather.windSpeed!=null&&t.jsxs("div",{children:["Wind: ",u.weather.windSpeed," km/h"]}),u.weather.swellHeight!=null&&t.jsxs("div",{children:["Swell: ",u.weather.swellHeight,"m"]}),u.weather.temperature!=null&&t.jsxs("div",{children:["Temp: ",u.weather.temperature,"°C"]}),A("stormglass")&&u.stormglassWeather&&t.jsxs(t.Fragment,{children:[t.jsx("div",{style:{fontWeight:"bold",marginTop:"4px",color:"#CC99CC"},children:"STORMGLASS"}),u.stormglassWeather.waveHeight!=null&&t.jsxs("div",{children:["Waves: ",u.stormglassWeather.waveHeight,"m"]}),u.stormglassWeather.visibility!=null&&t.jsxs("div",{children:["Vis: ",u.stormglassWeather.visibility,"km"]}),u.stormglassWeather.waterTemperature!=null&&t.jsxs("div",{children:["Water: ",u.stormglassWeather.waterTemperature,"°C"]})]})]})]}),t.jsx(eg,{title:"Location Manager",variant:"secondary",children:l?t.jsxs(ng,{children:[t.jsx("h3",{children:"Add New Location"}),t.jsx("p",{children:"Click on the map to set coordinates, then fill in the details below."}),t.jsx("input",{type:"text",placeholder:"Location Name",value:m.name,onChange:C=>d(W=>({...W,name:C.target.value}))}),t.jsxs("select",{value:m.category,onChange:C=>d(W=>({...W,category:C.target.value})),children:[t.jsx("option",{value:"fishing",children:"Fishing Spot"}),t.jsx("option",{value:"marina",children:"Marina"}),t.jsx("option",{value:"anchorage",children:"Anchorage"}),t.jsx("option",{value:"hazard",children:"Hazard"}),t.jsx("option",{value:"other",children:"Other"})]}),t.jsx("textarea",{placeholder:"Notes (optional)",value:m.notes,onChange:C=>d(W=>({...W,notes:C.target.value}))}),m.latitude!==null&&m.longitude!==null&&t.jsxs("div",{children:[t.jsx("h4",{style:{color:"#FF9966",marginBottom:"8px"},children:"Coordinates"}),t.jsxs("div",{style:{padding:"12px",backgroundColor:"#222222",borderRadius:"4px",border:"1px solid #333333",fontFamily:"monospace"},children:["Lat: ",m.latitude.toFixed(6),t.jsx("br",{}),"Lng: ",m.longitude.toFixed(6)]})]}),t.jsxs("div",{style:{fontSize:"12px",color:"#999",marginBottom:"8px"},children:["Status: ",m.name?"✓ Name":"❌ Need name"," |",m.latitude===null?" ❌ Need coords (click map)":" ✓ Coords"," |",R.isPending?" ⏳ Saving...":" ✓ Ready"]}),t.jsxs("div",{style:{display:"flex",gap:"8px"},children:[t.jsx(Q,{children:t.jsx(k,{onClick:B,disabled:!m.name||m.latitude===null||m.longitude===null||R.isPending,children:"Save Location"})}),t.jsx(k,{variant:"secondary",onClick:()=>{c(!1),d({name:"",category:"other",notes:"",latitude:null,longitude:null})},children:"Cancel"})]})]}):t.jsxs(t.Fragment,{children:[t.jsx(Q,{children:t.jsx(k,{onClick:()=>c(!0),disabled:R.isPending,children:"Add New Location"})}),h&&t.jsxs("div",{children:[t.jsx("h4",{style:{color:"#FF9966",marginBottom:"8px"},children:"Selected Location"}),t.jsxs("div",{style:{padding:"12px",backgroundColor:"#222222",borderRadius:"4px",border:"1px solid #333333"},children:[t.jsx("strong",{children:h.name}),t.jsx("br",{}),"Category: ",h.category,t.jsx("br",{}),"Coordinates: ",h.latitude.toFixed(6),", ",h.longitude.toFixed(6),t.jsx("br",{}),h.notes&&t.jsxs(t.Fragment,{children:["Notes: ",h.notes,t.jsx("br",{})]}),h.tags.length>0&&t.jsxs(t.Fragment,{children:["Tags: ",h.tags.join(", "),t.jsx("br",{})]}),t.jsx("div",{style:{marginTop:"8px"},children:t.jsx(Q,{children:t.jsx(k,{size:"sm",variant:"accent",onClick:()=>te(h.id),disabled:H.isPending,children:"Delete"})})})]})]}),t.jsx(tg,{children:U.map(C=>t.jsxs(rg,{children:[t.jsx("div",{className:"location-name",children:C.name}),t.jsx("div",{className:"location-category",children:C.category}),C.notes&&t.jsx("div",{className:"location-notes",children:C.notes}),t.jsx("div",{className:"location-actions",children:t.jsx(k,{size:"sm",onClick:()=>{y(C),j.current&&j.current.setView([C.latitude,C.longitude],15)},children:"View"})})]},C.id))})]})})]}),(D||F)&&t.jsx(E,{label:"System Status",value:"Loading chart data...",valueColor:"anakiwa"})]})},Gt=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Eo=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,cg=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: ${e=>e.theme.spacing.lg};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,dg=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${e=>e.theme.spacing.md};
`,pg=a.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
`,mg=a.div`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,hg=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xl};
  color: ${e=>e.theme.colors.text.muted};
  font-size: ${e=>e.theme.typography.fontSize.lg};
`,ug=()=>{const{data:e,isLoading:r,error:n}=Ta();if(r)return t.jsxs(Gt,{children:[t.jsx(q,{children:"Captain's License Progress"}),t.jsx(pg,{children:t.jsx(E,{label:"System Status",value:"Loading Progress Data...",valueColor:"neonCarrot",size:"lg"})})]});if(n)return t.jsxs(Gt,{children:[t.jsx(q,{children:"Captain's License Progress"}),t.jsx(mg,{children:t.jsx(Se,{type:"error",children:"Error loading license progress data. Please check your connection and try again."})})]});if(!e)return t.jsxs(Gt,{children:[t.jsx(q,{children:"Captain's License Progress"}),t.jsx(L,{title:"No Data",variant:"secondary",children:t.jsx(hg,{children:"No license progress data available yet. Log some trips to start tracking."})})]});const{totalDays:o,daysInLast3Years:s,totalHours:i,daysRemaining360:l,daysRemaining90In3Years:c,estimatedCompletion360:m}=e,d=o>=360,h=s>=90,y=d&&h;return t.jsxs(Gt,{children:[t.jsx(q,{children:"Captain's License Progress"}),y&&t.jsx(Se,{type:"success",children:"Congratulations! You have met all requirements for OUPV (6-pack) Captain's License eligibility."}),t.jsx(L,{title:"Current Sea Time Statistics",variant:"primary",children:t.jsxs(Eo,{children:[t.jsx(E,{label:"Total Sea Time Days",value:o,valueColor:"neonCarrot",size:"lg"}),t.jsx(E,{label:"Days (Last 3 Years)",value:s,valueColor:"lilac",size:"lg"}),t.jsx(E,{label:"Total Hours",value:i.toFixed(1),unit:"hrs",valueColor:"anakiwa",size:"lg"}),t.jsx(E,{label:"Average Hours/Day",value:o>0?(i/o).toFixed(1):"0.0",unit:"hrs",valueColor:"success",size:"lg"})]})}),t.jsxs(cg,{children:[t.jsx(L,{title:"360-Day Total Requirement",variant:"primary",children:t.jsx(er,{title:"Total Sea Time Days",current:o,target:360,unit:"days",color:"neonCarrot",size:"lg",showPercentage:!0})}),t.jsx(L,{title:"90-Day Recent Requirement",variant:"secondary",children:t.jsx(er,{title:"Days in Last 3 Years",current:s,target:90,unit:"days",color:"lilac",size:"lg",showPercentage:!0})})]}),t.jsx(L,{title:"Completion Estimates",variant:"accent",children:t.jsxs(dg,{children:[t.jsx(xr,{title:"360-Day Goal",estimatedDate:d?void 0:m??void 0,daysRemaining:d?void 0:l,isComplete:d,color:"neonCarrot",size:"md"}),t.jsx(xr,{title:"90-Day (3 Years) Goal",daysRemaining:h?void 0:c,isComplete:h,color:"lilac",size:"md"}),!y&&t.jsx(xr,{title:"License Eligibility",estimatedDate:m??void 0,isComplete:y,color:"anakiwa",size:"md"})]})}),t.jsx(L,{title:"OUPV (6-Pack) License Requirements",variant:"secondary",children:t.jsxs(Eo,{children:[t.jsx(E,{label:"Total Sea Time",value:"360 Days",valueColor:"neonCarrot",size:"md"}),t.jsx(E,{label:"Recent Experience",value:"90 Days in 3 Years",valueColor:"lilac",size:"md"}),t.jsx(E,{label:"Minimum Per Day",value:"4 Hours",valueColor:"anakiwa",size:"md"}),t.jsx(E,{label:"Additional Requirements",value:"Medical, Drug Test, Course",valueColor:"success",size:"md"})]})})]})},tn=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Lo=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,gg=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${e=>e.theme.spacing.lg};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,xg=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
  flex-wrap: wrap;
`,fg=a.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
`,yg=a.div`
  margin-bottom: ${e=>e.theme.spacing.lg};
`,zo=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,_t=a.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  gap: ${e=>e.theme.spacing.md};
  padding: ${e=>e.theme.spacing.sm};
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 1px solid ${e=>e.theme.colors.surface.light};
  border-radius: ${e=>e.theme.borderRadius.sm};
  align-items: center;

  &.header {
    background-color: ${e=>e.theme.colors.primary.neonCarrot};
    color: ${e=>e.theme.colors.text.inverse};
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    letter-spacing: 1px;
    font-size: ${e=>e.theme.typography.fontSize.sm};
  }
  
  &.overdue {
    border-color: ${e=>e.theme.colors.status.error};
    background-color: rgba(255, 102, 102, 0.1);
  }
  
  &.due-soon {
    border-color: ${e=>e.theme.colors.status.warning};
    background-color: rgba(255, 255, 102, 0.1);
  }
`,ne=a.div`
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  
  &.text {
    font-family: ${e=>e.theme.typography.fontFamily.primary};
  }
  
  &.status {
    font-weight: ${e=>e.theme.typography.fontWeight.bold};
    text-transform: uppercase;
  }
`,bg=()=>{const[e,r]=Ke.useState(""),{data:n,isLoading:o}=ye(),{data:s,isLoading:i,error:l}=Na(e||void 0),{data:c,isLoading:m,error:d}=yn(e||void 0),{data:h,isLoading:y,error:j}=Ba(e||void 0),f=o||i||m||y,p=l||d||j,g=b.useMemo(()=>{if(!s||!c||!h)return{totalTemplates:0,activeTemplates:0,upcomingCount:0,overdueCount:0,completedThisMonth:0,totalCostThisMonth:0,averageCost:0,completionRate:0};const x=new Date,v=new Date(x.getFullYear(),x.getMonth(),1),$=c.filter(F=>new Date(F.dueDate)<x).length,w=h.filter(F=>F.completedAt&&new Date(F.completedAt)>=v),A=w.reduce((F,R)=>F+(R.actualCost||0),0),T=h.filter(F=>F.actualCost&&F.actualCost>0),I=T.length>0?T.reduce((F,R)=>F+(R.actualCost||0),0)/T.length:0,D=c.length+h.length,U=D>0?h.length/D*100:0;return{totalTemplates:s.length,activeTemplates:s.filter(F=>F.isActive).length,upcomingCount:c.length,overdueCount:$,completedThisMonth:w.length,totalCostThisMonth:A,averageCost:I,completionRate:U}},[s,c,h]),u=b.useMemo(()=>{if(!c)return[];const x=new Date,v=new Date(x.getTime()+7*24*60*60*1e3);return c.map($=>{const w=new Date($.dueDate);let A="upcoming",T="Upcoming";return w<x?(A="overdue",T="Overdue"):w<=v&&(A="due-soon",T="Due Soon"),{...$,status:A,statusText:T,daysUntilDue:Math.ceil((w.getTime()-x.getTime())/(1e3*60*60*24))}}).sort(($,w)=>new Date($.dueDate).getTime()-new Date(w.dueDate).getTime())},[c]);return f?t.jsxs(tn,{children:[t.jsx(q,{children:"Maintenance Reports"}),t.jsx(fg,{children:t.jsx(E,{label:"System Status",value:"Loading Maintenance Data...",valueColor:"neonCarrot",size:"lg"})})]}):p?t.jsxs(tn,{children:[t.jsx(q,{children:"Maintenance Reports"}),t.jsx(yg,{children:t.jsx(Se,{type:"error",children:"Error loading maintenance data. Please check your connection and try again."})})]}):t.jsxs(tn,{children:[t.jsx(q,{children:"Maintenance Reports"}),t.jsxs(xg,{children:[t.jsx(k,{variant:e===""?"primary":"secondary",onClick:()=>r(""),children:"All Boats"}),n==null?void 0:n.map(x=>t.jsx(k,{variant:e===x.id?"primary":"secondary",onClick:()=>r(x.id),children:x.name},x.id))]}),t.jsx(L,{title:"Maintenance Overview",variant:"primary",children:t.jsxs(Lo,{children:[t.jsx(E,{label:"Active Templates",value:g.activeTemplates,valueColor:"neonCarrot",size:"lg"}),t.jsx(E,{label:"Upcoming Tasks",value:g.upcomingCount,valueColor:"anakiwa",size:"lg"}),t.jsx(E,{label:"Overdue Tasks",value:g.overdueCount,valueColor:g.overdueCount>0?"neonCarrot":"success",size:"lg"}),t.jsx(E,{label:"Completed This Month",value:g.completedThisMonth,valueColor:"success",size:"lg"})]})}),t.jsx(L,{title:"Cost Analysis",variant:"secondary",children:t.jsxs(Lo,{children:[t.jsx(E,{label:"Cost This Month",value:`$${g.totalCostThisMonth.toFixed(2)}`,valueColor:"lilac",size:"lg"}),t.jsx(E,{label:"Average Cost Per Task",value:`$${g.averageCost.toFixed(2)}`,valueColor:"lilac",size:"lg"}),t.jsx(E,{label:"Completion Rate",value:`${g.completionRate.toFixed(1)}%`,valueColor:"anakiwa",size:"lg"})]})}),t.jsxs(gg,{children:[t.jsx(L,{title:"Template Status",variant:"primary",children:t.jsx(er,{title:"Active Templates",current:g.activeTemplates,target:g.totalTemplates,unit:"templates",color:"neonCarrot",size:"md",showPercentage:!0})}),t.jsx(L,{title:"Task Completion",variant:"secondary",children:t.jsx(er,{title:"Completion Rate",current:g.completionRate,target:100,unit:"%",color:"lilac",size:"md",showPercentage:!1})})]}),u.length>0&&t.jsx(L,{title:"Upcoming Maintenance Tasks",variant:"accent",children:t.jsxs(zo,{children:[t.jsxs(_t,{className:"header",children:[t.jsx(ne,{children:"Task"}),t.jsx(ne,{children:"Boat"}),t.jsx(ne,{children:"Due Date"}),t.jsx(ne,{children:"Days Until Due"}),t.jsx(ne,{children:"Status"})]}),u.map(x=>{var v,$,w,A;return t.jsxs(_t,{className:x.status,children:[t.jsxs(ne,{className:"text",children:[((v=x.template)==null?void 0:v.title)||"Unknown Task",(($=x.template)==null?void 0:$.component)&&t.jsx("div",{style:{fontSize:"0.8em",color:"#999"},children:x.template.component})]}),t.jsx(ne,{className:"text",children:((A=(w=x.template)==null?void 0:w.boat)==null?void 0:A.name)||"Unknown"}),t.jsx(ne,{children:new Date(x.dueDate).toLocaleDateString()}),t.jsx(ne,{children:x.daysUntilDue>0?`${x.daysUntilDue} days`:`${Math.abs(x.daysUntilDue)} days ago`}),t.jsx(ne,{className:"status",children:x.statusText})]},x.id)})]})}),h&&h.length>0&&t.jsx(L,{title:"Recent Completions",variant:"secondary",children:t.jsxs(zo,{children:[t.jsxs(_t,{className:"header",children:[t.jsx(ne,{children:"Task"}),t.jsx(ne,{children:"Boat"}),t.jsx(ne,{children:"Completed"}),t.jsx(ne,{children:"Cost"}),t.jsx(ne,{children:"Time"})]}),h.slice(0,10).map(x=>{var v,$,w;return t.jsxs(_t,{children:[t.jsx(ne,{className:"text",children:((v=x.template)==null?void 0:v.title)||"Unknown Task"}),t.jsx(ne,{className:"text",children:((w=($=x.template)==null?void 0:$.boat)==null?void 0:w.name)||"Unknown"}),t.jsx(ne,{children:x.completedAt?new Date(x.completedAt).toLocaleDateString():"N/A"}),t.jsx(ne,{children:x.actualCost?`$${x.actualCost.toFixed(2)}`:"N/A"}),t.jsx(ne,{children:x.actualTime?`${x.actualTime}h`:"N/A"})]},x.id)})]})})]})},jg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 800px;
  margin: 0 auto;
`,vg=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${e=>e.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Do=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
  padding: ${e=>e.theme.spacing.lg};
  background-color: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.lg};
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.tanoi};
    background-color: ${e=>e.theme.colors.surface.medium};
  }

  &.secondary {
    border-color: ${e=>e.theme.colors.primary.lilac};

    &:hover {
      border-color: ${e=>e.theme.colors.primary.lilac};
    }
  }
`,Io=a.h2`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>e.theme.typography.fontSize.xl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0;

  .secondary & {
    color: ${e=>e.theme.colors.primary.lilac};
  }
`,Mo=a.p`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  line-height: ${e=>e.theme.typography.lineHeight.normal};
  margin: 0;
`,Ro=a.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,We=a.li`
  color: ${e=>e.theme.colors.text.muted};
  font-size: ${e=>e.theme.typography.fontSize.sm};

  &::before {
    content: '▶';
    color: ${e=>e.theme.colors.primary.neonCarrot};
    margin-right: ${e=>e.theme.spacing.sm};
    font-size: 0.8em;
  }

  .secondary &::before {
    color: ${e=>e.theme.colors.primary.lilac};
  }
`,$g=()=>{const e=de();return t.jsxs(jg,{children:[t.jsx(q,{children:"System Reports"}),t.jsx(L,{title:"Available Reports",variant:"primary",children:t.jsxs(vg,{children:[t.jsxs(Do,{onClick:()=>e("/reports/license"),children:[t.jsx(Io,{children:"Captain's License Progress"}),t.jsx(Mo,{children:"Track your progress toward OUPV (6-pack) Captain's License requirements"}),t.jsxs(Ro,{children:[t.jsx(We,{children:"360-day total sea time tracking"}),t.jsx(We,{children:"90-day recent experience monitoring"}),t.jsx(We,{children:"Progress charts and completion estimates"}),t.jsx(We,{children:"Detailed statistics and requirements"})]})]}),t.jsxs(Do,{className:"secondary",onClick:()=>e("/reports/maintenance"),children:[t.jsx(Io,{children:"Maintenance Reports"}),t.jsx(Mo,{children:"Comprehensive maintenance tracking and cost analysis for all vessels"}),t.jsxs(Ro,{children:[t.jsx(We,{children:"Upcoming and overdue task tracking"}),t.jsx(We,{children:"Cost analysis and completion rates"}),t.jsx(We,{children:"Template status and activity monitoring"}),t.jsx(We,{children:"Recent completion history"})]})]})]})}),t.jsx(L,{title:"Quick Access",variant:"accent",children:t.jsxs("div",{style:{display:"flex",gap:"16px",justifyContent:"center",flexWrap:"wrap"},children:[t.jsx(k,{variant:"primary",onClick:()=>e("/reports/license"),children:"License Progress"}),t.jsx(k,{variant:"secondary",onClick:()=>e("/reports/maintenance"),children:"Maintenance Reports"})]})})]})},wg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Cg=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${e=>e.theme.spacing.lg};
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Ft=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
  margin-bottom: ${e=>e.theme.spacing.md};
`,Et=a.label`
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,Sg=a.input`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`,No=a.div`
  padding: ${e=>e.theme.spacing.sm};
  border-radius: 4px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  
  ${e=>{switch(e.$type){case"success":return`
          background: ${e.theme.colors.status.success}20;
          color: ${e.theme.colors.status.success};
          border: 1px solid ${e.theme.colors.status.success};
        `;case"error":return`
          background: ${e.theme.colors.status.error}20;
          color: ${e.theme.colors.status.error};
          border: 1px solid ${e.theme.colors.status.error};
        `;case"info":return`
          background: ${e.theme.colors.primary.anakiwa}20;
          color: ${e.theme.colors.primary.anakiwa};
          border: 1px solid ${e.theme.colors.primary.anakiwa};
        `}}}
`,kg=a.div`
  display: grid;
  grid-template-columns: auto 1fr;
  gap: ${e=>e.theme.spacing.md};
  align-items: center;
`,rn=a.div`
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
`,nn=a.div`
  color: ${e=>e.theme.colors.text.primary};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
`,Tg=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
`,Ag=a.label`
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
  cursor: pointer;
`,Fg=a.span`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: ${e=>e.$checked?e.theme.colors.status.success:e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.$checked?e.theme.colors.status.success:e.theme.colors.primary.anakiwa};
  border-radius: 13px;
  transition: 0.3s;

  &::before {
    content: '';
    position: absolute;
    height: 18px;
    width: 18px;
    left: ${e=>e.$checked?"22px":"2px"};
    bottom: 2px;
    background-color: ${e=>e.theme.colors.text.primary};
    border-radius: 50%;
    transition: 0.3s;
  }
`,Eg=a.span`
  color: ${e=>e.$active?e.theme.colors.status.success:e.theme.colors.text.secondary};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,Lg=()=>{const{user:e,logout:r,isReadOnly:n}=sr(),o=de(),[s,i]=b.useState({currentPassword:"",newPassword:"",confirmPassword:""}),[l,c]=b.useState(!1),[m,d]=b.useState(null),[h,y]=b.useState({exists:!1,enabled:!1,username:""}),[j,f]=b.useState({username:"",password:""}),[p,g]=b.useState(!1),[u,x]=b.useState(null);Ke.useEffect(()=>{n||v()},[n]);const v=async()=>{try{const D=await O.getViewerSettings();y(D),f(U=>({...U,username:D.username||""}))}catch{}},$=async()=>{g(!0);try{if(h.exists){const D=await O.updateViewerSettings({enabled:!h.enabled});y(D),x({type:"success",text:`Viewer account ${D.enabled?"enabled":"disabled"}`})}else{if(!j.username||!j.password){x({type:"error",text:"Username and password required to create viewer account"}),g(!1);return}const D=await O.updateViewerSettings({username:j.username,password:j.password,enabled:!0});y(D),f(U=>({...U,password:""})),x({type:"success",text:"Viewer account created and enabled"})}}catch(D){x({type:"error",text:D.message||"Failed to update viewer settings"})}finally{g(!1)}},w=async D=>{if(D.preventDefault(),!j.username){x({type:"error",text:"Username is required"});return}if(!h.exists&&!j.password){x({type:"error",text:"Password is required for new viewer account"});return}if(j.password&&j.password.length<8){x({type:"error",text:"Password must be at least 8 characters"});return}g(!0),x({type:"info",text:"Saving..."});try{const U={username:j.username};j.password&&(U.password=j.password),h.exists||(U.enabled=!0);const F=await O.updateViewerSettings(U);y(F),f(R=>({...R,password:""})),x({type:"success",text:"Viewer account updated"})}catch(U){x({type:"error",text:U.message||"Failed to save viewer settings"})}finally{g(!1)}},A=D=>U=>{i(F=>({...F,[D]:U.target.value})),m&&d(null)},T=async D=>{if(D.preventDefault(),!s.currentPassword||!s.newPassword||!s.confirmPassword){d({type:"error",text:"All password fields are required"});return}if(s.newPassword!==s.confirmPassword){d({type:"error",text:"New passwords do not match"});return}if(s.newPassword.length<8){d({type:"error",text:"New password must be at least 8 characters"});return}c(!0),d({type:"info",text:"Changing password..."});try{await O.changePassword(s.currentPassword,s.newPassword),d({type:"success",text:"Password changed successfully. You will be logged out."}),i({currentPassword:"",newPassword:"",confirmPassword:""}),setTimeout(()=>{r()},2e3)}catch(U){d({type:"error",text:U.message||"Failed to change password"})}finally{c(!1)}},I=async()=>{window.confirm("Are you sure you want to log out?")&&await r()};return t.jsxs(wg,{children:[t.jsx(q,{children:"System Settings"}),t.jsxs(Cg,{children:[t.jsxs(L,{title:"User Account",children:[t.jsxs(kg,{children:[t.jsx(rn,{children:"Username:"}),t.jsx(nn,{children:(e==null?void 0:e.username)||"Unknown"}),t.jsx(rn,{children:"Account Created:"}),t.jsx(nn,{children:e!=null&&e.createdAt?new Date(e.createdAt).toLocaleDateString():"Unknown"}),t.jsx(rn,{children:"Last Updated:"}),t.jsx(nn,{children:e!=null&&e.updatedAt?new Date(e.updatedAt).toLocaleDateString():"Unknown"})]}),t.jsx("div",{style:{marginTop:"20px"},children:t.jsx(k,{onClick:I,variant:"secondary",children:"Logout"})})]}),t.jsx(Q,{fallback:t.jsx(L,{title:"Change Password",children:t.jsx("div",{style:{padding:"20px",color:"#6688CC",textAlign:"center",textTransform:"uppercase",letterSpacing:"1px"},children:"Password changes are not available for viewer accounts."})}),children:t.jsx(L,{title:"Change Password",children:t.jsxs("form",{onSubmit:T,children:[t.jsxs(Ft,{children:[t.jsx(Et,{htmlFor:"currentPassword",children:"Current Password"}),t.jsx(Lt,{id:"currentPassword",value:s.currentPassword,onChange:A("currentPassword"),disabled:l,autoComplete:"current-password"})]}),t.jsxs(Ft,{children:[t.jsx(Et,{htmlFor:"newPassword",children:"New Password"}),t.jsx(Lt,{id:"newPassword",value:s.newPassword,onChange:A("newPassword"),disabled:l,autoComplete:"new-password",minLength:8})]}),t.jsxs(Ft,{children:[t.jsx(Et,{htmlFor:"confirmPassword",children:"Confirm New Password"}),t.jsx(Lt,{id:"confirmPassword",value:s.confirmPassword,onChange:A("confirmPassword"),disabled:l,autoComplete:"new-password",minLength:8})]}),m&&t.jsx(No,{$type:m.type,children:m.text}),t.jsx("div",{style:{marginTop:"20px"},children:t.jsx(k,{type:"submit",disabled:l,children:l?"Changing Password...":"Change Password"})})]})})})]}),!n&&t.jsxs(L,{title:"Viewer Account",children:[t.jsx("div",{style:{marginBottom:"20px"},children:t.jsxs(Tg,{children:[t.jsx(Ag,{onClick:$,children:t.jsx(Fg,{$checked:h.enabled})}),t.jsx(Eg,{$active:h.enabled,children:h.enabled?"Enabled":"Disabled"}),p&&t.jsx("span",{style:{color:"#9999cc",fontSize:"12px"},children:"Updating..."})]})}),t.jsxs("form",{onSubmit:w,children:[t.jsxs(Ft,{children:[t.jsx(Et,{htmlFor:"viewerUsername",children:"Viewer Username"}),t.jsx(Sg,{id:"viewerUsername",type:"text",value:j.username,onChange:D=>{f(U=>({...U,username:D.target.value})),x(null)},disabled:p,placeholder:"viewer"})]}),t.jsxs(Ft,{children:[t.jsx(Et,{htmlFor:"viewerPassword",children:h.exists?"New Password (leave blank to keep)":"Password"}),t.jsx(Lt,{id:"viewerPassword",value:j.password,onChange:D=>{f(U=>({...U,password:D.target.value})),x(null)},disabled:p,minLength:8,placeholder:h.exists?"********":"Min 8 characters"})]}),u&&t.jsx(No,{$type:u.type,children:u.text}),t.jsx("div",{style:{marginTop:"20px"},children:t.jsx(k,{type:"submit",disabled:p,children:h.exists?"Update Viewer":"Create Viewer"})})]})]}),t.jsxs(L,{title:"System Management",children:[t.jsxs("div",{style:{display:"flex",gap:"10px",marginBottom:"20px"},children:[t.jsx(k,{onClick:()=>o("/settings/backup"),variant:"secondary",children:"Backup Manager"}),t.jsx(k,{onClick:()=>o("/settings/nautical"),variant:"secondary",children:"Nautical Data"})]}),t.jsxs("div",{style:{display:"grid",gridTemplateColumns:"1fr 1fr",gap:"10px"},children:[t.jsx(E,{label:"Interface Version",value:"LCARS v1.0",valueColor:"anakiwa"}),t.jsx(E,{label:"System Status",value:"Operational",valueColor:"success"}),t.jsx(E,{label:"API Endpoint",value:"http://localhost:8585/api/v1",valueColor:"anakiwa"}),t.jsx(E,{label:"Authentication",value:"JWT Token-based",valueColor:"lilac"})]})]})]})},zg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Dg=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
`,Po=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
`,Ig=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 1px solid ${e=>e.theme.colors.surface.medium};
  border-radius: ${e=>e.theme.borderRadius.sm};
  overflow: hidden;
`,Mg=a.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: ${e=>e.theme.spacing.md};
  cursor: pointer;

  &:hover {
    background: ${e=>e.theme.colors.surface.medium};
  }
`,Rg=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
  flex: 1;
`,Ng=a.span`
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
`,Pg=a.span`
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  background: ${e=>e.$tier==="free"?e.theme.colors.status.success+"30":e.theme.colors.primary.neonCarrot+"30"};
  color: ${e=>e.$tier==="free"?e.theme.colors.status.success:e.theme.colors.primary.neonCarrot};
  border: 1px solid ${e=>e.$tier==="free"?e.theme.colors.status.success:e.theme.colors.primary.neonCarrot};
`,Bg=a.span`
  color: ${e=>e.theme.colors.text.muted};
  font-size: ${e=>e.theme.typography.fontSize.sm};
`,Og=a.div`
  width: 48px;
  height: 24px;
  border-radius: 12px;
  background: ${e=>e.$active?e.theme.colors.status.success:e.theme.colors.surface.medium};
  border: 2px solid ${e=>e.$active?e.theme.colors.status.success:e.theme.colors.text.muted};
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &::after {
    content: '';
    position: absolute;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: ${e=>e.theme.colors.text.primary};
    top: 1px;
    left: ${e=>e.$active?"24px":"1px"};
    transition: left 0.2s ease;
  }
`,Ug=a.div`
  padding: 0 ${e=>e.theme.spacing.md} ${e=>e.theme.spacing.md};
  border-top: 1px solid ${e=>e.theme.colors.surface.medium};
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.md};
`,qg=a.p`
  color: ${e=>e.theme.colors.text.secondary};
  margin: ${e=>e.theme.spacing.sm} 0 0;
  line-height: 1.5;
`,Hg=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${e=>e.theme.spacing.md};

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
`,Bo=a.div``,Oo=a.div`
  color: ${e=>e.$type==="pro"?e.theme.colors.status.success:e.theme.colors.primary.neonCarrot};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
  margin-bottom: ${e=>e.theme.spacing.xs};
`,Uo=a.div`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  padding: 2px 0;

  &::before {
    content: '${e=>e.$type==="pro"?"+":"-"}';
    color: ${e=>e.$type==="pro"?e.theme.colors.status.success:e.theme.colors.primary.neonCarrot};
    margin-right: ${e=>e.theme.spacing.xs};
    font-weight: bold;
  }
`,Wg=a.input`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  width: 100%;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
  }

  &::placeholder {
    color: ${e=>e.theme.colors.text.muted};
  }
`,Vg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,Kg=a.label`
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${e=>e.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`,Gg=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  align-items: center;
`,qo=a.a`
  color: ${e=>e.theme.colors.primary.anakiwa};
  text-decoration: none;
  font-size: ${e=>e.theme.typography.fontSize.sm};

  &:hover {
    color: ${e=>e.theme.colors.primary.tanoi};
    text-decoration: underline;
  }
`,_g=a.div`
  color: ${e=>e.theme.colors.primary.lilac};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  font-style: italic;
`,Ho=({provider:e,enabled:r,apiKey:n,onToggle:o,onApiKeyChange:s})=>{const[i,l]=b.useState(!1);return t.jsxs(Ig,{$expanded:i,children:[t.jsxs(Mg,{onClick:()=>l(!i),children:[t.jsxs(Rg,{children:[t.jsx(Ng,{children:e.name}),t.jsx(Pg,{$tier:e.tier,children:e.tier}),!i&&t.jsx(Bg,{children:e.description.split(".")[0]})]}),t.jsx(Og,{$active:r,onClick:c=>{c.stopPropagation(),o()}})]}),i&&t.jsxs(Ug,{children:[t.jsx(qg,{children:e.description}),t.jsxs(Hg,{children:[t.jsxs(Bo,{children:[t.jsx(Oo,{$type:"pro",children:"Advantages"}),e.pros.map((c,m)=>t.jsx(Uo,{$type:"pro",children:c},m))]}),t.jsxs(Bo,{children:[t.jsx(Oo,{$type:"con",children:"Limitations"}),e.cons.map((c,m)=>t.jsx(Uo,{$type:"con",children:c},m))]})]}),e.requiresApiKey&&t.jsxs(Vg,{children:[t.jsx(Kg,{children:"API Key"}),t.jsx(Wg,{type:"password",placeholder:"Enter API key...",value:n||"",onChange:c=>s(c.target.value),onClick:c=>c.stopPropagation()}),e.apiKeySignupUrl&&t.jsx(qo,{href:e.apiKeySignupUrl,target:"_blank",rel:"noopener noreferrer",children:"Get an API key →"})]}),t.jsxs(Gg,{children:[t.jsx(qo,{href:e.website,target:"_blank",rel:"noopener noreferrer",children:"Visit website →"}),e.pricingNote&&t.jsx(_g,{children:e.pricingNote})]})]})]})},Qg=()=>{const e=de(),{isEnabled:r,getProviderConfig:n,toggleProvider:o,setApiKey:s}=lr();return t.jsxs(zg,{children:[t.jsx(Dg,{children:t.jsx(k,{variant:"secondary",size:"sm",onClick:()=>e("/settings"),children:"← Settings"})}),t.jsx(q,{children:"Nautical Data Providers"}),t.jsx(L,{title:"Free Providers",variant:"primary",children:t.jsx(Po,{children:zu.map(i=>t.jsx(Ho,{provider:i,enabled:r(i.id),apiKey:n(i.id).apiKey,onToggle:()=>o(i.id),onApiKeyChange:l=>s(i.id,l)},i.id))})}),t.jsx(L,{title:"Paid Providers",variant:"secondary",children:t.jsx(Po,{children:Du.map(i=>t.jsx(Ho,{provider:i,enabled:r(i.id),apiKey:n(i.id).apiKey,onToggle:()=>o(i.id),onApiKeyChange:l=>s(i.id,l)},i.id))})})]})},Jg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Yg=a.div`
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: ${e=>e.theme.spacing.lg};
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,Zg=a.div`
  padding: ${e=>e.theme.spacing.sm};
  border-radius: 4px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: ${e=>e.theme.spacing.md};
  
  ${e=>{switch(e.$type){case"success":return`
          background: ${e.theme.colors.status.success}20;
          color: ${e.theme.colors.status.success};
          border: 1px solid ${e.theme.colors.status.success};
        `;case"error":return`
          background: ${e.theme.colors.status.error}20;
          color: ${e.theme.colors.status.error};
          border: 1px solid ${e.theme.colors.status.error};
        `;case"info":return`
          background: ${e.theme.colors.primary.anakiwa}20;
          color: ${e.theme.colors.primary.anakiwa};
          border: 1px solid ${e.theme.colors.primary.anakiwa};
        `}}}
`,Xg=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,ex=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
  padding: ${e=>e.theme.spacing.md};
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
  }
`,tx=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.xs};
`,rx=a.div`
  color: ${e=>e.theme.colors.text.primary};
  font-weight: bold;
  font-family: ${e=>e.theme.typography.fontFamily.monospace};
`,nx=a.div`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  display: flex;
  gap: ${e=>e.theme.spacing.md};
`,ox=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.sm};
`,ax=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xl};
  color: ${e=>e.theme.colors.text.secondary};
  font-style: italic;
`,sx=()=>{const[e,r]=b.useState([]),[n,o]=b.useState(!0),[s,i]=b.useState(!1),[l,c]=b.useState(null);b.useEffect(()=>{m()},[]);const m=async()=>{try{o(!0);const f=await O.getBackups();r(f)}catch(f){c({type:"error",text:f.message||"Failed to load backups"})}finally{o(!1)}},d=async()=>{if(!s){i(!0),c({type:"info",text:"Creating backup... This may take a few minutes."});try{const f=await O.createBackup();c({type:"success",text:`Backup created successfully: ${f.filename}`}),await m()}catch(f){c({type:"error",text:f.message||"Failed to create backup"})}finally{i(!1)}}},h=async f=>{try{c({type:"info",text:`Downloading ${f.filename}...`});const p=await O.downloadBackup(f.id),g=window.URL.createObjectURL(p),u=document.createElement("a");u.href=g,u.download=f.filename,document.body.appendChild(u),u.click(),document.body.removeChild(u),window.URL.revokeObjectURL(g),c({type:"success",text:`Download started: ${f.filename}`})}catch(p){c({type:"error",text:p.message||"Failed to download backup"})}},y=f=>{if(f===0)return"0 Bytes";const p=1024,g=["Bytes","KB","MB","GB"],u=Math.floor(Math.log(f)/Math.log(p));return parseFloat((f/Math.pow(p,u)).toFixed(2))+" "+g[u]},j=f=>new Date(f).toLocaleString();return t.jsxs(Jg,{children:[t.jsx(q,{children:"Database Backup Manager"}),l&&t.jsx(Zg,{$type:l.type,children:l.text}),t.jsxs(Yg,{children:[t.jsxs(L,{title:"Backup Operations",children:[t.jsxs("div",{style:{marginBottom:"20px"},children:[t.jsx("div",{style:{width:"100%",marginBottom:"10px"},children:t.jsx(Q,{children:t.jsx(k,{onClick:d,disabled:s,children:s?"Creating Backup...":"Create Manual Backup"})})}),t.jsx("div",{style:{width:"100%"},children:t.jsx(k,{onClick:m,disabled:n,variant:"secondary",children:n?"Refreshing...":"Refresh List"})})]}),t.jsxs("div",{style:{display:"flex",flexDirection:"column",gap:"10px"},children:[t.jsx(E,{label:"Total Backups",value:e.length.toString(),valueColor:"anakiwa"}),t.jsx(E,{label:"Total Size",value:y(e.reduce((f,p)=>f+p.size,0)),valueColor:"lilac"}),t.jsx(E,{label:"Latest Backup",value:e.length>0?j(e[0].createdAt):"None",valueColor:"neonCarrot"})]}),t.jsxs("div",{style:{marginTop:"20px",padding:"10px",background:"rgba(255, 153, 102, 0.1)",border:"1px solid #FF9966"},children:[t.jsx("strong",{style:{color:"#FF9966"},children:"Important:"}),t.jsxs("ul",{style:{margin:"10px 0",paddingLeft:"20px",color:"#CCCCCC"},children:[t.jsx("li",{children:"Backups include both database records and uploaded photos"}),t.jsx("li",{children:"Large backups may take several minutes to create"}),t.jsx("li",{children:"Store backups in a secure location outside the system"}),t.jsx("li",{children:"Test backup restoration procedures regularly"})]})]})]}),t.jsx(L,{title:"Available Backups",children:n?t.jsx("div",{style:{textAlign:"center",padding:"40px"},children:t.jsx("div",{style:{color:"#6688CC"},children:"Loading backups..."})}):e.length===0?t.jsx(ax,{children:"No backups available. Create your first backup to get started."}):t.jsx(Xg,{children:e.map(f=>t.jsxs(ex,{children:[t.jsxs(tx,{children:[t.jsx(rx,{children:f.filename}),t.jsxs(nx,{children:[t.jsxs("span",{children:["Created: ",j(f.createdAt)]}),t.jsxs("span",{children:["Size: ",y(f.size)]})]})]}),t.jsx(ox,{children:t.jsx(k,{onClick:()=>h(f),variant:"secondary",size:"sm",children:"Download"})})]},f.id))})})]})]})},ix=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`,lx=a.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
`,cx=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
`,dx=a.h2`
  color: ${e=>e.theme.colors.primary.neonCarrot};
  font-size: ${e=>e.theme.typography.fontSize.xl};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0;
  min-width: 200px;
  text-align: center;
`,px=a.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background-color: ${e=>e.theme.colors.primary.anakiwa};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
`,mx=a.div`
  background-color: ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: ${e=>e.theme.spacing.sm};
  text-align: center;
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: ${e=>e.theme.typography.fontSize.sm};
`,hx=a.div`
  background-color: ${e=>e.theme.colors.surface.dark};
  min-height: 120px;
  padding: ${e=>e.theme.spacing.xs};
  display: flex;
  flex-direction: column;
  position: relative;
  
  ${e=>!e.$isCurrentMonth&&`
    background-color: ${e.theme.colors.surface.medium};
    opacity: 0.5;
  `}
  
  ${e=>e.$isToday&&`
    border: 2px solid ${e.theme.colors.primary.neonCarrot};
    background-color: ${e.theme.colors.primary.neonCarrot}10;
  `}

  ${e=>e.$hasEvents&&`
    border-left: 4px solid ${e.theme.colors.primary.lilac};
  `}
`,ux=a.div`
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  color: ${e=>e.$isToday?e.theme.colors.primary.neonCarrot:e.theme.colors.text.primary};
  margin-bottom: ${e=>e.theme.spacing.xs};
`,gx=a.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
`,Wo=a.div`
  background-color: ${e=>e.$type==="trip"?e.theme.colors.primary.anakiwa:e.theme.colors.primary.lilac};
  color: ${e=>e.theme.colors.text.primary};
  padding: 2px 4px;
  font-size: 10px;
  border-radius: 2px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`,xx=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,fx=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
`,Vo=a.div`
  display: flex;
  align-items: center;
  gap: ${e=>e.theme.spacing.xs};
  
  &::before {
    content: '';
    width: 12px;
    height: 12px;
    background-color: ${e=>e.$color};
    border-radius: 2px;
  }
`,yx=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],bx=["January","February","March","April","May","June","July","August","September","October","November","December"],jx=()=>{const[e,r]=b.useState(new Date),[n,o]=b.useState([]),{data:s,isLoading:i}=_e(),{data:l,isLoading:c}=yn();b.useEffect(()=>{const x=[];s&&s.forEach(v=>{var $;x.push({id:`trip-${v.id}`,title:`Trip: ${(($=v.boat)==null?void 0:$.name)||"Unknown Boat"}`,date:new Date(v.startTime),type:"trip",data:v})}),l&&l.forEach(v=>{var $;x.push({id:`maintenance-${v.id}`,title:`Maintenance: ${(($=v.template)==null?void 0:$.title)||"Unknown Task"}`,date:new Date(v.dueDate),type:"maintenance",data:v})}),o(x)},[s,l]);const m=x=>{r(v=>{const $=new Date(v);return x==="prev"?$.setMonth(v.getMonth()-1):$.setMonth(v.getMonth()+1),$})},d=()=>{r(new Date)},h=x=>{const v=x.getFullYear(),$=x.getMonth(),w=new Date(v,$,1),T=new Date(v,$+1,0).getDate(),I=w.getDay(),D=[];for(let F=I-1;F>=0;F--){const R=new Date(v,$,-F);D.push(R)}for(let F=1;F<=T;F++)D.push(new Date(v,$,F));const U=42-D.length;for(let F=1;F<=U;F++)D.push(new Date(v,$+1,F));return D},y=x=>n.filter(v=>new Date(v.date).toDateString()===x.toDateString()),j=x=>{const v=new Date;return x.toDateString()===v.toDateString()},f=x=>x.getMonth()===e.getMonth(),p=h(e),g=(s==null?void 0:s.filter(x=>{const v=new Date(x.startTime);return v.getMonth()===e.getMonth()&&v.getFullYear()===e.getFullYear()}))||[],u=(l==null?void 0:l.filter(x=>{const v=new Date(x.dueDate);return v.getMonth()===e.getMonth()&&v.getFullYear()===e.getFullYear()}))||[];return t.jsxs(ix,{children:[t.jsx(q,{children:"Mission Calendar"}),t.jsxs(xx,{children:[t.jsx(E,{label:"Current Month Trips",value:g.length.toString(),valueColor:"anakiwa"}),t.jsx(E,{label:"Upcoming Maintenance",value:u.length.toString(),valueColor:"lilac"}),t.jsx(E,{label:"Total Events",value:(g.length+u.length).toString(),valueColor:"neonCarrot"})]}),t.jsxs(L,{title:"Calendar View",children:[t.jsxs(lx,{children:[t.jsxs(cx,{children:[t.jsx(k,{onClick:()=>m("prev"),variant:"secondary",size:"sm",children:"← Previous"}),t.jsxs(dx,{children:[bx[e.getMonth()]," ",e.getFullYear()]}),t.jsx(k,{onClick:()=>m("next"),variant:"secondary",size:"sm",children:"Next →"})]}),t.jsx(k,{onClick:d,size:"sm",children:"Today"})]}),t.jsxs(fx,{children:[t.jsx(Vo,{$color:"#6688CC",children:"Trips"}),t.jsx(Vo,{$color:"#CC99CC",children:"Maintenance"})]}),t.jsxs(px,{children:[yx.map(x=>t.jsx(mx,{children:x},x)),p.map((x,v)=>{const $=y(x);return t.jsxs(hx,{$isCurrentMonth:f(x),$isToday:j(x),$hasEvents:$.length>0,children:[t.jsx(ux,{$isToday:j(x),children:x.getDate()}),t.jsxs(gx,{children:[$.slice(0,3).map(w=>t.jsx(Wo,{$type:w.type,title:w.title,children:w.title},w.id)),$.length>3&&t.jsxs(Wo,{$type:"trip",children:["+",$.length-3," more"]})]})]},v)})]}),(i||c)&&t.jsx("div",{style:{textAlign:"center",padding:"20px",color:"#6688CC"},children:"Loading calendar data..."})]})]})},vx=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`,$x=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  align-items: center;
  margin-bottom: ${e=>e.theme.spacing.md};
  flex-wrap: wrap;
`,wx=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-top: ${e=>e.theme.spacing.md};
`,Cx=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  border-radius: ${e=>e.theme.borderRadius.sm};
  overflow: hidden;
  cursor: pointer;
  transition: all ${e=>e.theme.animation.normal} ease;

  &:hover {
    border-color: ${e=>e.theme.colors.primary.neonCarrot};
    transform: translateY(-2px);
    box-shadow: ${e=>e.theme.shadows.glow};
  }
`,Sx=a.img`
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
`,kx=a.div`
  padding: ${e=>e.theme.spacing.sm};
`,Tx=a.div`
  color: ${e=>e.theme.colors.text.primary};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  margin-bottom: ${e=>e.theme.spacing.xs};
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
`,Ax=a.div`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.xs};
  display: flex;
  justify-content: space-between;
  align-items: center;
`,Fx=a.span`
  background: ${e=>e.theme.colors.primary.anakiwa};
  color: ${e=>e.theme.colors.text.primary};
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 10px;
  text-transform: uppercase;
  font-weight: bold;
`,Ex=a.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: ${e=>e.$isOpen?"flex":"none"};
  align-items: center;
  justify-content: center;
  z-index: ${e=>e.theme.zIndex.modal};
  padding: ${e=>e.theme.spacing.lg};
`,Lx=a.div`
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${e=>e.theme.spacing.md};
`,zx=a.img`
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
`,Dx=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
  padding: ${e=>e.theme.spacing.md};
  border-radius: ${e=>e.theme.borderRadius.sm};
  color: ${e=>e.theme.colors.text.primary};
  text-align: center;
  max-width: 500px;
`,Ix=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
`,Mx=a.div`
  text-align: center;
  padding: ${e=>e.theme.spacing.xl};
  color: ${e=>e.theme.colors.text.secondary};
  font-style: italic;
`,Rx=a.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  display: ${e=>e.$isOpen?"flex":"none"};
  align-items: center;
  justify-content: center;
  z-index: ${e=>e.theme.zIndex.modal};
  padding: ${e=>e.theme.spacing.lg};
`,Nx=a.div`
  background: ${e=>e.theme.colors.surface.dark};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  border-radius: ${e=>e.theme.borderRadius.md};
  padding: ${e=>e.theme.spacing.lg};
  max-width: 500px;
  width: 100%;
`,on=a.div`
  margin-bottom: ${e=>e.theme.spacing.md};

  label {
    display: block;
    color: ${e=>e.theme.colors.primary.anakiwa};
    font-weight: bold;
    text-transform: uppercase;
    font-size: ${e=>e.theme.typography.fontSize.sm};
    margin-bottom: ${e=>e.theme.spacing.xs};
  }

  select, input[type="file"] {
    width: 100%;
    padding: ${e=>e.theme.spacing.sm};
    background: ${e=>e.theme.colors.surface.medium};
    border: 1px solid ${e=>e.theme.colors.primary.anakiwa};
    border-radius: ${e=>e.theme.borderRadius.sm};
    color: ${e=>e.theme.colors.text.primary};
    font-family: ${e=>e.theme.typography.fontFamily.primary};
  }
`,Px=a.div`
  margin-top: ${e=>e.theme.spacing.md};
  text-align: center;

  img {
    max-width: 100%;
    max-height: 200px;
    border: 2px solid ${e=>e.theme.colors.primary.anakiwa};
    border-radius: ${e=>e.theme.borderRadius.sm};
  }
`,Bx=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${e=>e.theme.spacing.lg};
`,Ox=a.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${e=>e.theme.spacing.md};
  margin-bottom: ${e=>e.theme.spacing.lg};
`,Ux=()=>{const[e,r]=b.useState([]),[n,o]=b.useState([]),[s,i]=b.useState("all"),[l,c]=b.useState(null),[m,d]=b.useState(!0),[h,y]=b.useState(!1),[j,f]=b.useState(null),[p,g]=b.useState(null),[u,x]=b.useState("trip"),[v,$]=b.useState(""),[w,A]=b.useState(!1),T=b.useRef(null),{data:I,isLoading:D,refetch:U}=_e();b.useEffect(()=>{const N=[];I&&I.forEach(C=>{C.photos&&C.photos.forEach(W=>{var K;N.push({...W,contextType:"trip",contextTitle:`Trip: ${((K=C.boat)==null?void 0:K.name)||"Unknown Boat"}`,contextDate:new Date(C.startTime).toLocaleDateString()})})}),N.sort((C,W)=>new Date(W.createdAt).getTime()-new Date(C.createdAt).getTime()),r(N),d(D)},[I,D]),b.useEffect(()=>{let N=e;s==="trips"&&(N=e.filter(C=>C.contextType==="trip")),o(N)},[e,s]);const F=N=>{c(N)},R=()=>{c(null)},H=N=>{if(!l)return;const C=n.findIndex(K=>K.id===l.id);let W=C;N==="prev"?W=C>0?C-1:n.length-1:W=C<n.length-1?C+1:0,c(n[W])},G=N=>{if(N===0)return"0 Bytes";const C=1024,W=["Bytes","KB","MB","GB"],K=Math.floor(Math.log(N)/Math.log(C));return parseFloat((N/Math.pow(C,K)).toFixed(2))+" "+W[K]},J=e.filter(N=>N.contextType==="trip"),B=N=>{var W;const C=(W=N.target.files)==null?void 0:W[0];if(C){f(C);const K=new FileReader;K.onload=le=>{var ae;g((ae=le.target)==null?void 0:ae.result)},K.readAsDataURL(C)}},te=async()=>{if(!j||!v){alert("Please select a file and choose what to attach it to");return}A(!0);try{await O.uploadPhoto(j,u,v),f(null),g(null),$(""),y(!1),U(),alert("Photo uploaded successfully!")}catch(N){console.error("Failed to upload photo:",N),alert("Failed to upload photo. Please try again.")}finally{A(!1)}},be=()=>{y(!1),f(null),g(null),$("")};return t.jsxs(vx,{children:[t.jsx(q,{children:"Photo Gallery"}),t.jsxs(Ox,{children:[t.jsx(E,{label:"Total Photos",value:e.length.toString(),valueColor:"neonCarrot"}),t.jsx(E,{label:"Trip Photos",value:J.length.toString(),valueColor:"anakiwa"}),t.jsx(E,{label:"Maintenance Photos",value:"0",valueColor:"lilac"}),t.jsx(E,{label:"Total Size",value:G(e.reduce((N,C)=>N+(C.sizeBytes||0),0)),valueColor:"anakiwa"})]}),t.jsxs(L,{title:"Photo Collection",children:[t.jsxs($x,{children:[t.jsx(Q,{children:t.jsx(k,{onClick:()=>y(!0),variant:"accent",size:"sm",children:"Upload Photo"})}),t.jsxs(k,{onClick:()=>i("all"),variant:s==="all"?"primary":"secondary",size:"sm",children:["All Photos (",e.length,")"]}),t.jsxs(k,{onClick:()=>i("trips"),variant:s==="trips"?"primary":"secondary",size:"sm",children:["Trip Photos (",J.length,")"]}),t.jsx(k,{onClick:()=>i("trips"),variant:s==="trips"?"primary":"secondary",size:"sm",disabled:!0,children:"Maintenance Photos (Coming Soon)"})]}),m?t.jsx("div",{style:{textAlign:"center",padding:"40px"},children:t.jsx("div",{style:{color:"#6688CC"},children:"Loading photos..."})}):n.length===0?t.jsx(Mx,{children:"No photos found. Photos will appear here when you attach them to trips."}):t.jsx(wx,{children:n.map(N=>t.jsxs(Cx,{onClick:()=>F(N),children:[t.jsx(Sx,{src:N.webOptimizedPath||N.originalPath,alt:N.contextTitle,loading:"lazy"}),t.jsxs(kx,{children:[t.jsx(Tx,{children:N.contextTitle}),t.jsxs(Ax,{children:[t.jsx(Fx,{$type:N.contextType,children:N.contextType}),t.jsx("span",{children:N.contextDate})]})]})]},N.id))})]}),t.jsx(Ex,{$isOpen:!!l,onClick:R,children:l&&t.jsxs(Lx,{onClick:N=>N.stopPropagation(),children:[t.jsx(zx,{src:l.webOptimizedPath||l.originalPath,alt:l.contextTitle}),t.jsxs(Dx,{children:[t.jsx("div",{style:{marginBottom:"10px"},children:t.jsx("strong",{children:l.contextTitle})}),t.jsxs("div",{style:{fontSize:"14px",color:"#CCCCCC"},children:[t.jsxs("div",{children:["Date: ",l.contextDate]}),t.jsxs("div",{children:["Size: ",G(l.sizeBytes||0)]}),t.jsxs("div",{children:["Type: ",l.mimeType]}),l.metadata&&t.jsxs("div",{children:["Dimensions: ",l.metadata.width," × ",l.metadata.height]})]})]}),t.jsxs(Ix,{children:[t.jsx(k,{onClick:()=>H("prev"),variant:"secondary",size:"sm",children:"← Previous"}),t.jsx(k,{onClick:R,size:"sm",children:"Close"}),t.jsx(k,{onClick:()=>H("next"),variant:"secondary",size:"sm",children:"Next →"})]})]})}),t.jsx(Rx,{$isOpen:h,onClick:be,children:t.jsxs(Nx,{onClick:N=>N.stopPropagation(),children:[t.jsx(q,{level:3,children:"Upload Photo"}),t.jsxs(on,{children:[t.jsx("label",{children:"Select Photo"}),t.jsx("input",{type:"file",accept:"image/*",onChange:B,ref:T})]}),p&&t.jsx(Px,{children:t.jsx("img",{src:p,alt:"Preview"})}),t.jsxs(on,{children:[t.jsx("label",{children:"Attach To"}),t.jsx("select",{value:u,onChange:N=>{x(N.target.value),$("")},children:t.jsx("option",{value:"trip",children:"Trip"})})]}),t.jsxs(on,{children:[t.jsxs("label",{children:["Select ",u==="trip"?"Trip":"Item"]}),t.jsxs("select",{value:v,onChange:N=>$(N.target.value),children:[t.jsx("option",{value:"",children:"-- Select --"}),u==="trip"&&(I==null?void 0:I.map(N=>{var C;return t.jsxs("option",{value:N.id,children:[((C=N.boat)==null?void 0:C.name)||"Unknown Boat"," - ",new Date(N.startTime).toLocaleDateString()]},N.id)}))]})]}),t.jsxs(Bx,{children:[t.jsx(k,{variant:"secondary",onClick:be,disabled:w,children:"Cancel"}),t.jsx(k,{onClick:te,disabled:!j||!v||w,children:w?"Uploading...":"Upload"})]})]})})]})},qx=a.div`
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`,Ko=a.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${e=>e.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`,ce=a.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: ${e=>e.theme.spacing.sm};
`,M=a.li`
  color: ${e=>e.theme.colors.text.primary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  line-height: 1.6;
  padding-left: 1.5rem;
  position: relative;

  &::before {
    content: '●';
    position: absolute;
    left: 0;
    color: ${e=>e.theme.colors.primary.neonCarrot};
  }
`,Z=a.p`
  color: ${e=>e.theme.colors.text.secondary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  line-height: 1.8;
  margin: 0 0 ${e=>e.theme.spacing.md} 0;
`,se=a.h4`
  color: ${e=>e.theme.colors.primary.anakiwa};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.md};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: ${e=>e.theme.spacing.md} 0 ${e=>e.theme.spacing.sm} 0;
`,me=a.span`
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: ${e=>e.theme.typography.fontSize.xs};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-left: 8px;
  ${e=>{switch(e.$status){case"working":return`background: ${e.theme.colors.status.success}30; color: ${e.theme.colors.status.success}; border: 1px solid ${e.theme.colors.status.success};`;case"limited":return`background: ${e.theme.colors.status.warning}30; color: ${e.theme.colors.status.warning}; border: 1px solid ${e.theme.colors.status.warning};`;case"untested":return`background: ${e.theme.colors.primary.anakiwa}30; color: ${e.theme.colors.primary.anakiwa}; border: 1px solid ${e.theme.colors.primary.anakiwa};`;case"removed":return`background: ${e.theme.colors.status.error}30; color: ${e.theme.colors.status.error}; border: 1px solid ${e.theme.colors.status.error};`}}}
`,Hx=a.div`
  display: flex;
  gap: ${e=>e.theme.spacing.sm};
  flex-wrap: wrap;
  margin-bottom: ${e=>e.theme.spacing.md};
`,gt=a.button`
  background: ${e=>e.$active?e.theme.colors.primary.neonCarrot:e.theme.colors.surface.dark};
  color: ${e=>e.$active?e.theme.colors.text.inverse:e.theme.colors.primary.neonCarrot};
  border: 2px solid ${e=>e.theme.colors.primary.neonCarrot};
  padding: ${e=>e.theme.spacing.sm} ${e=>e.theme.spacing.md};
  font-family: ${e=>e.theme.typography.fontFamily.primary};
  font-size: ${e=>e.theme.typography.fontSize.sm};
  font-weight: ${e=>e.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  border-radius: 0 16px 16px 0;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    filter: brightness(1.2);
  }
`,Wx=()=>{const[e,r]=b.useState("overview");return t.jsxs(qx,{children:[t.jsx(q,{level:2,color:"neonCarrot",withBar:!0,barColor:"tanoi",children:"Ship's Computer — Technical Manual"}),t.jsxs(Hx,{children:[t.jsx(gt,{$active:e==="overview",onClick:()=>r("overview"),children:"Overview"}),t.jsx(gt,{$active:e==="trips",onClick:()=>r("trips"),children:"Trips & GPS"}),t.jsx(gt,{$active:e==="navigation",onClick:()=>r("navigation"),children:"Navigation"}),t.jsx(gt,{$active:e==="nautical",onClick:()=>r("nautical"),children:"Nautical Data"}),t.jsx(gt,{$active:e==="maintenance",onClick:()=>r("maintenance"),children:"Maintenance"}),t.jsx(gt,{$active:e==="other",onClick:()=>r("other"),children:"Other Features"})]}),e==="overview"&&t.jsx(t.Fragment,{children:t.jsxs(L,{title:"System Overview",variant:"primary",children:[t.jsx(Z,{children:"Captain's Log is a comprehensive boat tracking and management application. It provides GPS trip recording, nautical chart overlays, marine weather data, vessel maintenance tracking, and more. The system operates with dual connection modes — local network and remote via secure tunnel — with automatic failover."}),t.jsxs(Ko,{children:[t.jsxs("div",{children:[t.jsx(se,{children:"Core Systems"}),t.jsxs(ce,{children:[t.jsx(M,{children:"GPS trip recording with foreground service"}),t.jsx(M,{children:"Interactive map with nautical chart overlays"}),t.jsx(M,{children:"Marine weather and tide data"}),t.jsx(M,{children:"Vessel maintenance scheduling and tracking"}),t.jsx(M,{children:"Captain's notes and ship's log"}),t.jsx(M,{children:"Photo gallery with geo-tagging"})]})]}),t.jsxs("div",{children:[t.jsx(se,{children:"Support Systems"}),t.jsxs(ce,{children:[t.jsx(M,{children:"Dual connection mode (local + remote)"}),t.jsx(M,{children:"Offline data storage with automatic sync"}),t.jsx(M,{children:"TLS certificate pinning for security"}),t.jsx(M,{children:"To-do list management"}),t.jsx(M,{children:"Calendar integration"}),t.jsx(M,{children:"Data backup and restore"})]})]})]})]})}),e==="trips"&&t.jsxs(t.Fragment,{children:[t.jsxs(L,{title:"Trip Recording",variant:"primary",children:[t.jsx(Z,{children:"Record your voyages with continuous GPS tracking. The app runs a foreground service that captures your position at configurable intervals (default: every 5 seconds) and plots your route on the map."}),t.jsx(se,{children:"How to Record a Trip"}),t.jsxs(ce,{children:[t.jsx(M,{children:'Navigate to Trip Log and tap "New Trip"'}),t.jsx(M,{children:"Enter trip details (name, vessel, type)"}),t.jsx(M,{children:"GPS tracking starts automatically via foreground service"}),t.jsx(M,{children:"A persistent notification shows tracking status"}),t.jsx(M,{children:"End the trip when you arrive — route is saved and displayed on the map"})]}),t.jsx(se,{children:"Stop Point Detection"}),t.jsx(Z,{children:"The system automatically detects when you've stopped by monitoring if you remain within a 45-foot radius for more than 5 minutes. Stop points are marked on your trip route."})]}),t.jsxs(L,{title:"Marked Locations",variant:"secondary",children:[t.jsx(Z,{children:"Save important locations on the map — fishing spots, marinas, anchorages, hazards, or custom waypoints. Each location can include a name, category, notes, and tags."}),t.jsx(se,{children:"How to Mark a Location"}),t.jsxs(ce,{children:[t.jsx(M,{children:"Open the Map view"}),t.jsx(M,{children:'Tap the "+" button while at your current location'}),t.jsx(M,{children:"Enter name, select category (fishing, marina, anchorage, hazard, other)"}),t.jsx(M,{children:"Add optional notes and tags"}),t.jsx(M,{children:"Locations appear as category-specific icons on the map"})]})]})]}),e==="navigation"&&t.jsx(t.Fragment,{children:t.jsxs(L,{title:"Map & Navigation",variant:"accent",children:[t.jsx(Z,{children:"The interactive map supports multiple base maps, overlay layers, and data sources. Use the layer controls (top-left button) to switch between map modes and toggle data visibility."}),t.jsx(se,{children:"Base Maps"}),t.jsxs(ce,{children:[t.jsxs(M,{children:[t.jsx("strong",{children:"Standard (OSM)"})," — Default OpenStreetMap tiles, always available"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"NOAA Charts"})," — Official US nautical charts with depth soundings, hazards, and navigation aids. Enable in Settings → Nautical Providers. US coastal waters only."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"GEBCO Bathymetry"})," — Global ocean depth visualization. Slow initial load but tiles are cached. Best at zoom levels 3-12."]})]}),t.jsx(se,{children:"Overlays"}),t.jsx(Z,{children:"Overlays render on top of the standard base map. They are hidden when using NOAA Charts or GEBCO as the base map."}),t.jsx(ce,{children:t.jsxs(M,{children:[t.jsx("strong",{children:"OpenSeaMap"})," — Nautical marks, buoys, lights, and seamark symbols. Community-maintained with global coverage. See openseamap.org/legend for symbol meanings."]})}),t.jsx(se,{children:"Map Controls"}),t.jsxs(ce,{children:[t.jsxs(M,{children:[t.jsx("strong",{children:"Layer button"})," (top-left) — Opens the control panel"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Base Map"})," — Switch between Standard, NOAA Charts, and GEBCO"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Overlays"})," — Toggle tile overlays on/off (Standard base map only)"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Nautical Data"})," — Toggle data providers (tides, weather, alerts, etc.)"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Map Data"})," — Show/hide trips and marked locations"]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Refresh"})," — Reload all nautical data for current viewport"]})]})]})}),e==="nautical"&&t.jsxs(t.Fragment,{children:[t.jsxs(L,{title:"Free Nautical Providers",variant:"primary",children:[t.jsx(Z,{children:"Configure nautical data providers in Settings → Nautical Providers. Each provider can be independently enabled/disabled. NOAA providers are grouped together. Once enabled, toggle visibility on the map using the layer controls."}),t.jsxs(se,{children:["OpenSeaMap ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Nautical marks, buoys, lights, and seamark overlays on OpenStreetMap. Community-maintained with global coverage. Appears as a tile overlay on the standard base map. Visit openseamap.org/legend to understand the symbols."}),t.jsxs(se,{children:["NOAA Charts ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Official US coastal nautical charts from NOAA showing depths, hazards, channels, and aids to navigation. Selectable as a base map. Initial load is slow but tiles are cached for subsequent use. Tiles around your GPS location are preloaded on app startup."}),t.jsxs(se,{children:["GEBCO Bathymetry ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Global bathymetry and ocean depth visualization via WMS. Selectable as a base map. Very slow initial load (WMS server limitation) but tiles are cached after first view. Best viewed at zoom levels 3-12; becomes pixelated beyond zoom 12."}),t.jsxs(se,{children:["NOAA CO-OPS ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Real-time and predicted tide data from US stations. When enabled, tide station markers appear on the map (clock icons). Tap a station marker to see the station name and current tide prediction. Data covers US coastal waters. Best viewed around Chesapeake Bay, San Francisco Bay, or other major US ports."}),t.jsxs(se,{children:["NOAA Weather Alerts ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Active marine weather alerts from the National Weather Service. When alerts exist at your map location, colored zone polygons appear on the map (red for extreme/severe, orange for moderate, yellow for advisory). A dismissible banner card shows alert details at the top of the screen. Warning markers are placed at the center of each alert zone for easy discovery while browsing. Background polling every 15 minutes sends push notifications for new alerts."}),t.jsxs(se,{children:["Open-Meteo Marine ",t.jsx(me,{$status:"working",children:"Working"})]}),t.jsx(Z,{children:"Marine weather forecasts including wave height, swell, wind speed, and temperature. When enabled, a crosshair appears at the map center showing where data is sampled, and a weather card in the bottom-left corner displays current conditions. Global coverage, no API key required."}),t.jsxs(se,{children:["Open-Meteo Ocean ",t.jsx(me,{$status:"limited",children:"Limited"})]}),t.jsx(Z,{children:"Ocean current velocity, direction, and sea surface temperature data. The API is functional but returns null data for most locations. When data is available, it appears in the weather card. Coverage may improve as Open-Meteo expands their marine dataset."}),t.jsxs(se,{children:["AISstream ",t.jsx(me,{$status:"untested",children:"Not Tested"})]}),t.jsx(Z,{children:"Real-time AIS vessel tracking via WebSocket. Requires a free API key from aisstream.io. When enabled, vessel positions appear as arrow markers on the map showing heading and speed. Service has been reported as intermittently unavailable."})]}),t.jsxs(L,{title:"Paid Nautical Providers",variant:"info",children:[t.jsx(Z,{children:"The following providers require paid API keys and have not been tested in the current release."}),t.jsxs(ce,{children:[t.jsxs(M,{children:[t.jsx("strong",{children:"WorldTides"})," ",t.jsx(me,{$status:"untested",children:"Untested"})," — Global tide predictions. $10 for 5,000 predictions."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Stormglass"})," ",t.jsx(me,{$status:"untested",children:"Untested"})," — Premium marine weather from multiple sources. Free tier: 10 req/day, paid from $19/month."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Windy"})," ",t.jsx(me,{$status:"untested",children:"Untested"})," — Animated wind/wave/weather overlays. ~$720/year."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"Navionics/Garmin"})," ",t.jsx(me,{$status:"untested",children:"Untested"})," — Premium nautical charts with detailed depth data. Contact for pricing."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"MarineTraffic"})," ",t.jsx(me,{$status:"untested",children:"Untested"})," — Global vessel tracking with satellite AIS. Credit-based pricing."]})]})]}),t.jsx(L,{title:"Removed Providers",variant:"secondary",children:t.jsxs(ce,{children:[t.jsxs(M,{children:[t.jsx("strong",{children:"USCG NAVCEN / Wrecks & Obstructions"})," ",t.jsx(me,{$status:"removed",children:"Removed"})," — USCG data source URL returned 404. Replaced with NOAA AWOIS which only had 359 records concentrated off the Carolina coast. Too sparse to be useful."]}),t.jsxs(M,{children:[t.jsx("strong",{children:"OpenSeaMap Depth"})," ",t.jsx(me,{$status:"removed",children:"Removed"})," — Depth sounding tile overlay. Tile server returns empty transparent images globally. Service is effectively dead."]})]})})]}),e==="maintenance"&&t.jsx(t.Fragment,{children:t.jsxs(L,{title:"Maintenance Tracking",variant:"primary",children:[t.jsx(Z,{children:"Track vessel maintenance with templates and scheduled events. Set up recurring maintenance items and get notified when service is due."}),t.jsx(se,{children:"Maintenance Templates"}),t.jsxs(ce,{children:[t.jsx(M,{children:"Create templates for recurring maintenance (oil changes, hull cleaning, etc.)"}),t.jsx(M,{children:"Set intervals (by time or engine hours)"}),t.jsx(M,{children:"Track costs and parts"}),t.jsx(M,{children:"View maintenance history and reports"})]}),t.jsx(se,{children:"Maintenance Events"}),t.jsxs(ce,{children:[t.jsx(M,{children:"Log completed maintenance with date, cost, and notes"}),t.jsx(M,{children:"Attach photos to maintenance records"}),t.jsx(M,{children:"View upcoming and overdue maintenance"}),t.jsx(M,{children:"Generate maintenance cost reports"})]})]})}),e==="other"&&t.jsx(t.Fragment,{children:t.jsxs(Ko,{children:[t.jsxs(L,{title:"Captain's Notes",variant:"accent",children:[t.jsx(Z,{children:"Keep a ship's log with rich text notes. Notes sync across devices and can be searched, filtered, and organized."}),t.jsxs(ce,{children:[t.jsx(M,{children:"Create and edit notes with the built-in editor"}),t.jsx(M,{children:"Search notes by content"}),t.jsx(M,{children:"Notes sync automatically when online"})]})]}),t.jsxs(L,{title:"To-Do Lists",variant:"secondary",children:[t.jsx(Z,{children:"Manage boat-related tasks with to-do lists. Track completion and organize by priority."}),t.jsxs(ce,{children:[t.jsx(M,{children:"Create tasks with descriptions"}),t.jsx(M,{children:"Mark tasks complete"}),t.jsx(M,{children:"Filter by status"})]})]}),t.jsxs(L,{title:"Photo Gallery",variant:"primary",children:[t.jsx(Z,{children:"Capture and organize photos from your voyages. Photos are geo-tagged and can be associated with trips."}),t.jsxs(ce,{children:[t.jsx(M,{children:"Take photos directly from the app"}),t.jsx(M,{children:"Automatic geo-tagging with GPS coordinates"}),t.jsx(M,{children:"WiFi-only uploads to save mobile data"}),t.jsx(M,{children:"7-day local retention after upload"})]})]}),t.jsxs(L,{title:"Reports & Calendar",variant:"info",children:[t.jsx(Z,{children:"View trip statistics, maintenance reports, and license progress. The calendar view shows upcoming maintenance and past trips."}),t.jsxs(ce,{children:[t.jsx(M,{children:"Trip summary and statistics"}),t.jsx(M,{children:"Maintenance cost analysis"}),t.jsx(M,{children:"License/certification progress tracking"}),t.jsx(M,{children:"Calendar view of events and maintenance"})]})]}),t.jsxs(L,{title:"Backup & Restore",variant:"accent",children:[t.jsx(Z,{children:"Back up your data and restore from previous backups. Ensures you never lose your ship's records."}),t.jsxs(ce,{children:[t.jsx(M,{children:"Manual and automatic backups"}),t.jsx(M,{children:"Restore from any backup point"}),t.jsx(M,{children:"Export data for external use"})]})]}),t.jsxs(L,{title:"Security",variant:"secondary",children:[t.jsx(Z,{children:"The app uses multiple layers of security to protect your data in transit and at rest."}),t.jsxs(ce,{children:[t.jsx(M,{children:"TLS certificate pinning (SHA-256)"}),t.jsx(M,{children:"Encrypted local storage (EncryptedSharedPreferences)"}),t.jsx(M,{children:"API key authentication"}),t.jsx(M,{children:"HTTPS-only communication"})]})]})]})})]})},Qt=a.div`
  min-height: 100vh;
  background-color: ${e=>e.theme.colors.background};
  color: ${e=>e.theme.colors.text.primary};
`,Vx=a.div`
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: ${e=>e.theme.colors.background};
  
  .loading-text {
    color: ${e=>e.theme.colors.primary.neonCarrot};
    font-size: 24px;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 2px;
  }
`;function Kx(){const{isAuthenticated:e,isLoading:r,needsSetup:n}=sr();return r?t.jsx(Qt,{children:t.jsx(Vx,{children:t.jsx("div",{className:"loading-text",children:"Initializing LCARS Interface"})})}):n?t.jsx(Qt,{children:t.jsx(jr,{})}):e?t.jsx(Qt,{children:t.jsx(Pl,{children:t.jsx(Il,{children:t.jsxs(vn,{children:[t.jsx(_,{path:"/",element:t.jsx(Hn,{})}),t.jsx(_,{path:"/dashboard",element:t.jsx(Ec,{})}),t.jsx(_,{path:"/boats",element:t.jsx(Od,{})}),t.jsx(_,{path:"/boats/new",element:t.jsx(cp,{})}),t.jsx(_,{path:"/boats/:id",element:t.jsx(Xd,{})}),t.jsx(_,{path:"/trips",element:t.jsx(wp,{})}),t.jsx(_,{path:"/trips/new",element:t.jsx(om,{})}),t.jsx(_,{path:"/trips/:id",element:t.jsx(Vp,{})}),t.jsx(_,{path:"/trips/:id/edit",element:t.jsx(Zp,{})}),t.jsx(_,{path:"/notes",element:t.jsx(jm,{})}),t.jsx(_,{path:"/notes/new",element:t.jsx(mo,{})}),t.jsx(_,{path:"/notes/:id",element:t.jsx(Lm,{})}),t.jsx(_,{path:"/notes/:id/edit",element:t.jsx(mo,{})}),t.jsx(_,{path:"/todos",element:t.jsx(_h,{})}),t.jsx(_,{path:"/maintenance",element:t.jsx(su,{})}),t.jsx(_,{path:"/maintenance/templates/new",element:t.jsx(Fo,{})}),t.jsx(_,{path:"/maintenance/templates/:id",element:t.jsx(hu,{})}),t.jsx(_,{path:"/maintenance/templates/:id/edit",element:t.jsx(Fo,{})}),t.jsx(_,{path:"/maintenance/events/:id",element:t.jsx($u,{})}),t.jsx(_,{path:"/map",element:t.jsx(lg,{})}),t.jsx(_,{path:"/reports",element:t.jsx($g,{})}),t.jsx(_,{path:"/reports/license",element:t.jsx(ug,{})}),t.jsx(_,{path:"/reports/maintenance",element:t.jsx(bg,{})}),t.jsx(_,{path:"/settings",element:t.jsx(Lg,{})}),t.jsx(_,{path:"/settings/backup",element:t.jsx(sx,{})}),t.jsx(_,{path:"/settings/nautical",element:t.jsx(Qg,{})}),t.jsx(_,{path:"/calendar",element:t.jsx(jx,{})}),t.jsx(_,{path:"/photos",element:t.jsx(Ux,{})}),t.jsx(_,{path:"/docs",element:t.jsx(Wx,{})}),t.jsx(_,{path:"*",element:t.jsx(Hn,{})})]})})})}):t.jsx(Qt,{children:t.jsxs(vn,{children:[t.jsx(_,{path:"/setup",element:t.jsx(jr,{})}),t.jsx(_,{path:"*",element:t.jsx(jr,{})})]})})}const Gx=ts`
  /* Import Antonio font from Google Fonts - full weight range for LCARS */
  @import url('https://fonts.googleapis.com/css2?family=Antonio:wght@100;200;300;400;500;600;700&display=swap');

  /* CSS Reset */
  *, *::before, *::after {
    box-sizing: border-box;
  }

  * {
    margin: 0;
    padding: 0;
  }

  html, body {
    height: 100%;
    overflow-x: hidden;
  }

  /* Body - Authentic LCARS styling */
  body {
    font-family: 'Antonio', sans-serif;
    font-size: 14px;
    line-height: 1.4;
    color: #FF9933;
    background-color: #000000;
    text-transform: uppercase;
    letter-spacing: 1px;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  #root {
    min-height: 100vh;
  }

  /* Headings - LCARS style */
  h1, h2, h3, h4, h5, h6 {
    font-family: 'Antonio', sans-serif;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1.5px;
    color: #FF9933;
  }

  h1 {
    font-size: 2.5rem;
    letter-spacing: 2px;
  }

  h2 {
    font-size: 2rem;
  }

  h3 {
    font-size: 1.75rem;
  }

  h4 {
    font-size: 1.5rem;
  }

  h5, h6 {
    font-size: 1.25rem;
  }

  /* Button reset */
  button {
    font-family: 'Antonio', sans-serif;
    font-size: inherit;
    text-transform: uppercase;
    border: none;
    background: none;
    cursor: pointer;
    color: inherit;
  }

  /* Form inputs - LCARS style */
  input, textarea, select {
    font-family: 'Antonio', sans-serif;
    font-size: inherit;
    text-transform: none;
    color: #FFCC99;
    background-color: #0A0A0A;
    border: 2px solid #664466;
    border-radius: 0;
    padding: 8px 16px;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    &:focus {
      outline: none;
      border-color: #FF9933;
      box-shadow: 0 0 8px rgba(255, 153, 51, 0.4);
    }

    &::placeholder {
      color: #664466;
    }
  }

  /* Links - LCARS blue */
  a {
    color: #99CCFF;
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: #FFCC99;
    }
  }

  /* Scrollbar - LCARS flat design */
  ::-webkit-scrollbar {
    width: 10px;
  }

  ::-webkit-scrollbar-track {
    background: #0A0A0A;
  }

  ::-webkit-scrollbar-thumb {
    background: #664466;
    border-radius: 0;

    &:hover {
      background: #CC99CC;
    }
  }

  /* Selection styling */
  ::selection {
    background-color: #FF9933;
    color: #000000;
  }

  ::-moz-selection {
    background-color: #FF9933;
    color: #000000;
  }

  /* LCARS Animations */
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes slideIn {
    from {
      transform: translateX(-100%);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }

  @keyframes lcars-blink {
    0%, 50% {
      opacity: 1;
    }
    51%, 100% {
      opacity: 0.3;
    }
  }

  @keyframes lcars-sweep {
    0% {
      background-position: -100% 0;
    }
    100% {
      background-position: 200% 0;
    }
  }

  @keyframes lcars-pulse {
    0%, 100% {
      box-shadow: 0 0 4px rgba(255, 153, 51, 0.4);
    }
    50% {
      box-shadow: 0 0 12px rgba(255, 153, 51, 0.8);
    }
  }

  /* Animation utility classes */
  .fade-in {
    animation: fadeIn 0.3s ease-in-out;
  }

  .slide-in {
    animation: slideIn 0.4s ease-out;
  }

  .lcars-blink {
    animation: lcars-blink 1s infinite;
  }

  .lcars-sweep {
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(255, 153, 51, 0.3) 50%,
      transparent 100%
    );
    background-size: 200% 100%;
    animation: lcars-sweep 2s linear infinite;
  }

  .lcars-pulse {
    animation: lcars-pulse 2s ease-in-out infinite;
  }

  /* Utility classes */
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }

  .text-uppercase {
    text-transform: uppercase;
  }

  .text-center {
    text-align: center;
  }

  .text-right {
    text-align: right;
  }

  /* Responsive adjustments */
  @media (max-width: 768px) {
    body {
      font-size: 13px;
    }

    h1 {
      font-size: 2rem;
    }

    h2 {
      font-size: 1.75rem;
    }

    h3 {
      font-size: 1.5rem;
    }
  }

  @media (max-width: 480px) {
    body {
      font-size: 12px;
      letter-spacing: 0.5px;
    }

    h1 {
      font-size: 1.75rem;
    }

    h2 {
      font-size: 1.5rem;
    }
  }
`,_x={colors:{primary:{paleCanary:"#FFFF99",tanoi:"#FFCC99",goldenTanoi:"#FFCC66",neonCarrot:"#FF9933",eggplant:"#664466",lilac:"#CC99CC",anakiwa:"#99CCFF",mariner:"#3366CC",bahamBlue:"#006699"},background:"#000000",surface:{dark:"#0A0A0A",medium:"#1A1119",light:"#2A2233"},text:{primary:"#FF9933",secondary:"#CC99CC",muted:"#664466",inverse:"#000000",light:"#FFCC99"},status:{success:"#55FF55",warning:"#FFFF99",error:"#FF5555",info:"#99CCFF"},interactive:{hover:"#FFCC66",active:"#FFCC99",disabled:"#664466"}},typography:{fontFamily:{primary:"'Antonio', 'Helvetica Neue', Arial, sans-serif",monospace:"'Courier New', monospace"},fontSize:{xs:"11px",sm:"13px",md:"15px",lg:"18px",xl:"24px",xxl:"32px",xxxl:"48px"},fontWeight:{normal:400,bold:700},lineHeight:{tight:1.1,normal:1.4,loose:1.7},letterSpacing:{tight:"-0.02em",normal:"0.04em",wide:"0.1em",extraWide:"0.2em"}},spacing:{xs:"4px",sm:"8px",md:"16px",lg:"24px",xl:"32px",xxl:"48px",xxxl:"64px"},borderRadius:{none:"0",sm:"4px",md:"8px",lg:"16px",xl:"24px",pill:"9999px"},shadows:{sm:"0 1px 3px rgba(255, 153, 51, 0.12)",md:"0 4px 8px rgba(255, 153, 51, 0.15)",lg:"0 10px 20px rgba(255, 153, 51, 0.18)",glow:"0 0 20px rgba(255, 153, 51, 0.35)",glowStrong:"0 0 40px rgba(255, 153, 51, 0.5)",glowSubtle:"0 0 10px rgba(255, 153, 51, 0.15)"},zIndex:{dropdown:1e3,sticky:1020,fixed:1030,modal:1040,popover:1050,tooltip:1060},breakpoints:{sm:"640px",md:"768px",lg:"1024px",xl:"1280px",xxl:"1536px"},animation:{fast:"150ms",normal:"300ms",slow:"500ms"},lcars:{sidebarWidth:"200px",headerHeight:"60px",footerHeight:"40px",elbowSize:"60px",barThickness:"30px",buttonHeight:"40px",gap:"3px",buttonRadius:"9999px"}},Qx=new Ja({defaultOptions:{queries:{retry:3,staleTime:5*60*1e3,refetchOnWindowFocus:!1}}});sn.createRoot(document.getElementById("root")).render(t.jsx(Ke.StrictMode,{children:t.jsx(Ya,{client:Qx,children:t.jsx(es,{children:t.jsxs(rs,{theme:_x,children:[t.jsx(Gx,{}),t.jsx(md,{children:t.jsx(Kx,{})})]})})})}));
//# sourceMappingURL=index-GUSYlJAM.js.map
