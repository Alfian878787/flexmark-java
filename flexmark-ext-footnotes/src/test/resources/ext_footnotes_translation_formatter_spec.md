---
title: Footnotes Extension Formatter Spec
author: Vladimir Schneider
version: 0.1
date: '2017-01-27'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Footnotes

Converts footnote references and definitions to footnotes in the HTML page. A footnote is a link
reference starting with a ^ with the definition being the same: [^ref]: footnote.

A footnote definition is a container block that can contain any element as long as it is
indented by 4 spaces or a tab from the indent level of the footnote.

basic

```````````````````````````````` example(Footnotes: 1) options(details)
text [^footnote] embedded.

[^footnote]: footnote text
with continuation

.
--------------------------
text [^_1_] embedded.

[^_1_]: footnote text
    with continuation
--------------------------
<<<text [^_1_] embedded.
>>>teEXt [^_1_] eEmBeEDDeED.
<<<footnote text
with continuation
>>>FootNoteE teEXt
WiIth coNtiINuUaAtiIoN
--------------------------
teEXt [^_1_] eEmBeEDDeED.

[^_1_]: FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN
--------------------------
teEXt [^footnote] eEmBeEDDeED.

[^footnote]: FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN
````````````````````````````````


undefined

```````````````````````````````` example Footnotes: 2
text [^undefined] embedded.

[^footnote]: footnote text
with continuation


.
teEXt [^undefined] eEmBeEDDeED.

[^footnote]: FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN
````````````````````````````````


duplicated

```````````````````````````````` example Footnotes: 3
text [^footnote] embedded.

[^footnote]: footnote text
with continuation

[^footnote]: duplicated footnote text
with continuation

.
teEXt [^footnote] eEmBeEDDeED.

[^footnote]: FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN

[^footnote]: DuUpLiIcaAteED FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN
````````````````````````````````


nested

```````````````````````````````` example Footnotes: 4
text [^footnote] embedded.

[^footnote]: footnote text with [^another] embedded footnote
with continuation

[^another]: footnote text
with continuation

.
teEXt [^footnote] eEmBeEDDeED.

[^footnote]: FootNoteE teEXt WiIth [^another] eEmBeEDDeED FootNoteE
    WiIth coNtiINuUaAtiIoN

[^another]: FootNoteE teEXt
    WiIth coNtiINuUaAtiIoN
````````````````````````````````


circular

```````````````````````````````` example Footnotes: 5
text [^footnote] embedded.

[^footnote]: footnote text with [^another] embedded footnote
with continuation

[^another]: footnote text with [^another] embedded footnote
with continuation

.
teEXt [^footnote] eEmBeEDDeED.

[^footnote]: FootNoteE teEXt WiIth [^another] eEmBeEDDeED FootNoteE
    WiIth coNtiINuUaAtiIoN

[^another]: FootNoteE teEXt WiIth [^] eEmBeEDDeED FootNoteE
    WiIth coNtiINuUaAtiIoN
````````````````````````````````


compound

```````````````````````````````` example Footnotes: 6
This paragraph has a footnote[^footnote].

[^footnote]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
.
thiIS paARaAGRaAph haAS aA FootNoteE[^footnote].

[^footnote]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS.

    - iIteEm 1
    - iIteEm 2
````````````````````````````````


Not a footnote nor a footnote definition if space between [ and ^.

```````````````````````````````` example Footnotes: 7
This paragraph has no footnote[ ^footnote].

[ ^footnote]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
.
thiIS paARaAGRaAph haAS No FootNoteE[ ^FootNoteE].

[ ^FootNoteE]: thiIS iIS theE BoDyY oF theE FootNoteE.
WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
**BoLD**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
````````````````````````````````


Unused footnotes are not used and do not show up on the page.

```````````````````````````````` example Footnotes: 8
This paragraph has a footnote[^2].

[^1]: This is the body of the unused footnote.
with continuation text. Inline _italic_ and 
**bold**.

[^2]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^1]: thiIS iIS theE BoDyY oF theE uUNuUSeED FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS.

    - iIteEm 1
    - iIteEm 2
````````````````````````````````


Undefined footnotes are rendered as if they were text, with emphasis left as is.

```````````````````````````````` example Footnotes: 9
This paragraph has a footnote[^**footnote**].

[^footnote]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
.
thiIS paARaAGRaAph haAS aA FootNoteE[^**footnote**].

[^footnote]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS.

    - iIteEm 1
    - iIteEm 2
````````````````````````````````


Footnote numbers are assigned in order of their reference in the document

```````````````````````````````` example Footnotes: 10
This paragraph has a footnote[^2]. Followed by another[^1]. 

[^1]: This is the body of the unused footnote.
with continuation text. Inline _italic_ and 
**bold**.

[^2]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists.
    
    - item 1
    - item 2
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2]. foLLoWeED ByY aANotheER[^1].

[^1]: thiIS iIS theE BoDyY oF theE uUNuUSeED FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS.

    - iIteEm 1
    - iIteEm 2
````````````````````````````````


Footnotes can contain references to other footnotes.

```````````````````````````````` example Footnotes: 11
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists and footnotes[^1].
    
    - item 1
    - item 2
    
    [^1]: This is the body of a nested footnote.
    with continuation text. Inline _italic_ and 
    **bold**.
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS aAND FootNoteES[^1].

    - iIteEm 1
    - iIteEm 2

    [^1]: thiIS iIS theE BoDyY oF aA NeESteED FootNoteE.
        WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
        **BoLD**.
````````````````````````````````


Customized strings

```````````````````````````````` example Footnotes: 12
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.
with continuation text. Inline _italic_ and 
**bold**.

    Multiple paragraphs are supported as other 
    markdown elements such as lists and footnotes[^1].
    
    - item 1
    - item 2
    
    [^1]: This is the body of a nested footnote.
    with continuation text. Inline _italic_ and 
    **bold**.
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
    WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
    **BoLD**.

    muULtiIpLeE paARaAGRaAphS aAReE SuUppoRteED aAS otheER
    maARKDoWN eELeEmeENtS SuUch aAS LiIStS aAND FootNoteES[^1].

    - iIteEm 1
    - iIteEm 2

    [^1]: thiIS iIS theE BoDyY oF aA NeESteED FootNoteE.
        WiIth coNtiINuUaAtiIoN teEXt. iNLiINeE _iItaALiIc_ aAND
        **BoLD**.
````````````````````````````````


Customized link ref class

```````````````````````````````` example Footnotes: 13
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
````````````````````````````````


Customized link ref class

```````````````````````````````` example Footnotes: 14
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.
````````````````````````````````


Parser emulation family indent handling is ignored. Otherwise the indent can be huge.

```````````````````````````````` example Footnotes: 15
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.

      Another paragraph of the footnote
      
    Also a paragraph of the footnote
    
        indented code of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

    aLSo aA paARaAGRaAph oF theE FootNoteE

        indented code of the footnote
````````````````````````````````


List item indent is used

```````````````````````````````` example Footnotes: 16
This paragraph has a footnote[^2].  

[^2]: This is the body of the footnote.

        Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

        Another paragraph of the footnote
        
            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


## Placement Options

```````````````````````````````` example Placement Options: 1
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 2) options(references-document-top)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 3) options(references-document-bottom)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented cod`e of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented cod`e of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 4) options(references-group-with-first)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 5) options(references-group-with-last)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 6) options(references-document-bottom, references-sort)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


```````````````````````````````` example(Placement Options: 7) options(references-document-bottom, references-sort-unused-last)
This paragraph has a footnote[^1].  

[^2]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^1].  

[^3]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
       
This paragraph has a footnote[^3].  

[^1]: This is the body of the footnote.

    Another paragraph of the footnote
    
            indented code of the footnote
      
Not a paragraph of the footnote
.
thiIS paARaAGRaAph haAS aA FootNoteE[^1].

[^2]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^2].

[^3]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE

thiIS paARaAGRaAph haAS aA FootNoteE[^].

[^1]: thiIS iIS theE BoDyY oF theE FootNoteE.

    aNotheER paARaAGRaAph oF theE FootNoteE

            indented code of the footnote

not aA paARaAGRaAph oF theE FootNoteE
````````````````````````````````


