
public class POSTagger {

}

enum TAGSET
{
	
	CC,	// Coordinating conjunction e.g. and,but,or...
	CD,	// Cardinal Number 
	DT,	// Determiner 
	EX,	// Existential there 
	FW,	// Foreign Word 
	IN, // Preposition or subordinating conjunction 
	JJ, // Adjective 
	JJR,	// Adjective, comparative 
	JJS,	// Adjective, superlative 
	LS,	 //List Item Marker 
	MD, //	 Modal e.g. can, could, might, may...
	NN,	// Noun, singular or mass 
	NNP, // Proper Noun, singular 
	NNPS, // Proper Noun, plural 
	NNS,  // Noun, plural 
	PDT, //	 Predeterminer e.g. all, both ... when they precede an article
	POS, //	 Possessive Ending e.g. Nouns ending in 's
	PRP, //	 Personal Pronoun e.g. I, me, you, he...
	PRP$, //	 Possessive Pronoun e.g. my, your, mine, yours...
	RB, //	 Adverb Most words that end in -ly as well as degree words like quite, too and very
	RBR, //	 Adverb, comparative Adverbs with the comparative ending -er, with a strictly comparative meaning.
	RBS, //	 Adverb, superlative 
	RP,	// Particle 
	SYM,	// Symbol Should be used for mathematical, scientific or technical symbols
	TO,	//to 
	UH,	// Interjectione.g. uh, well, yes, my...
	VB, //	 Verb, base form subsumes imperatives, infinitives and subjunctives
	VBD, //	 Verb, past tense includes the conditional form of the verb to be
	VBG, //	 Verb, gerund or persent participle 
	VBN, // Verb, past participle 
	VBP, //	 Verb, non-3rd person singular present 
	VBZ, //	 Verb, 3rd person singular present 
	WDT, //	 Wh-determiner e.g. which, and that when it is used as a relative pronoun
	WP, //	 Wh-pronoun e.g. what, who, whom...
	WP$, //	 Possessive wh-pronoun e.g.
	WRB //	 Wh-adverb  e.g. how, where why
}